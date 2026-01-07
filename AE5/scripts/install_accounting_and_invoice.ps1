Param(
    [string]$url = 'http://localhost:8069',
    [string]$db = 'garantias',
    [string]$user = 'admin@example.com',
    [string]$pass = 'admin'
)

function rpc_callkw($model, $method, $args, $websession){
    $params = @{ model = $model; method = $method; args = @( $args ); kwargs = @{} }
    $body = @{ jsonrpc = '2.0'; method = 'call'; params = $params }
    $resp = Invoke-RestMethod -Uri "$url/web/dataset/call_kw" -Method Post -Body ($body | ConvertTo-Json -Depth 12) -ContentType 'application/json' -WebSession $websession -ErrorAction Stop
    Write-Host "RPC $model.$method -> raw response:" ($resp | ConvertTo-Json -Depth 10)
    return $resp
}

Write-Host "Conectando a Odoo: $url (db: $db)"
$websession = New-Object Microsoft.PowerShell.Commands.WebRequestSession
$authBody = @{ jsonrpc = '2.0'; method = 'call'; params = @{ db = $db; login = $user; password = $pass } }
$authResp = Invoke-RestMethod -Uri "$url/web/session/authenticate" -Method Post -Body ($authBody | ConvertTo-Json -Depth 6) -ContentType 'application/json' -WebSession $websession -ErrorAction Stop
if (-not $authResp.result) { Write-Host "Auth failed: $($authResp | ConvertTo-Json)"; exit 1 }
Write-Host "Autenticado. UID: $($authResp.result.uid)"

# Buscar módulos relacionados con contabilidad
$domain = @(@(@('name','in',@('account','account_accountant'))))
$searchReadParams = @{ domain = $domain; fields = @('id','name','state','shortdesc') }
$modulesResp = Invoke-RestMethod -Uri "$url/web/dataset/call_kw/ir.module.module/search_read" -Method Post -Body (@{ jsonrpc='2.0'; method='call'; params=$searchReadParams } | ConvertTo-Json -Depth 8) -ContentType 'application/json' -WebSession $websession -ErrorAction Stop
Write-Host "Módulos encontrados:" ($modulesResp | ConvertTo-Json -Depth 10)

$toInstall = @()
if ($modulesResp.result -and $modulesResp.result.Count -gt 0) {
    foreach ($m in $modulesResp.result) {
        Write-Host "Module: $($m.name) state=$($m.state) id=$($m.id)"
        if ($m.state -ne 'installed') { $toInstall += $m.id }
    }
} else {
    Write-Host "No se encontraron módulos 'account' o 'account_accountant' en la lista. Intentando buscar por 'account' parcial..."
    $searchParams2 = @{ domain = @(@(@('name','ilike','account'))); fields = @('id','name','state','shortdesc') }
    $modulesResp2 = Invoke-RestMethod -Uri "$url/web/dataset/call_kw/ir.module.module/search_read" -Method Post -Body (@{ jsonrpc='2.0'; method='call'; params=$searchParams2 } | ConvertTo-Json -Depth 8) -ContentType 'application/json' -WebSession $websession -ErrorAction Stop
    Write-Host ($modulesResp2 | ConvertTo-Json -Depth 10)
    foreach ($m in $modulesResp2.result) { if ($m.state -ne 'installed') { $toInstall += $m.id } }
}

if ($toInstall.Count -eq 0) {
    Write-Host "No hay módulos pendientes de instalar (o ya instalados)."
} else {
    Write-Host "IDs a instalar: $toInstall"
    foreach ($id in $toInstall) {
        Write-Host "Intentando instalar módulo id=$id..."
        $installResp = Invoke-RestMethod -Uri "$url/web/dataset/call_kw/ir.module.module/button_immediate_install" -Method Post -Body (@{ jsonrpc='2.0'; method='call'; params = @{ args = @( @( $id ) ) } } | ConvertTo-Json -Depth 8) -ContentType 'application/json' -WebSession $websession -ErrorAction SilentlyContinue
        Write-Host "Install response:" ($installResp | ConvertTo-Json -Depth 10)
    }
}

# Esperar y comprobar si account.move está disponible
Write-Host "Esperando 5 segundos para que el servidor actualice módulos..."
Start-Sleep -Seconds 5

# Verificar existencia del modelo account.move intentando search_read
try {
    $testBody = @{ jsonrpc='2.0'; method='call'; params = @{ model = 'account.move'; method = 'search_read'; args = @(@()); kwargs = @{ fields = @('id','name'); limit=1 } } }
    $testResp = Invoke-RestMethod -Uri "$url/web/dataset/call_kw/account.move/search_read" -Method Post -Body ($testBody | ConvertTo-Json -Depth 10) -ContentType 'application/json' -WebSession $websession -ErrorAction Stop
    Write-Host "Modelo account.move accesible. Resp:" ($testResp | ConvertTo-Json -Depth 10)
    $accountAvailable = $true
} catch {
    Write-Host "Modelo account.move NO está accesible aún. Error: $_" -ForegroundColor Yellow
    $accountAvailable = $false
}

if ($accountAvailable) {
    Write-Host "Creando partner/producto/factura de prueba..."
    $partnerResp = rpc_callkw 'res.partner' 'create' @( @{ name = 'Cliente Demo'; email = 'cliente@example.com' } ) $websession
    $partnerId = $partnerResp.result
    Write-Host "Partner creado: $partnerId"

    $productResp = rpc_callkw 'product.product' 'create' @( @{ name = 'Bicicleta Demo'; list_price = 1000.0 } ) $websession
    $productId = $productResp.result
    Write-Host "Product creado: $productId"

    # Crear factura
    $lineValues = @{ name = 'Bicicleta Demo'; product_id = $productId; quantity = 1; price_unit = 1000 }
    $moveValues = @{ move_type = 'out_invoice'; invoice_date = (Get-Date -Format yyyy-MM-dd); partner_id = $partnerId; invoice_line_ids = @( @(0,0,$lineValues) ) }
    $moveResp = rpc_callkw 'account.move' 'create' @( $moveValues ) $websession
    $moveId = $moveResp.result
    Write-Host "Factura creada: $moveId"

    if ($moveId) {
        $postResp = rpc_callkw 'account.move' 'action_post' @( @( $moveId ) ) $websession
        Write-Host "Post response:" ($postResp | ConvertTo-Json -Depth 10)
    }
} else {
    Write-Host "No se pudo acceder a account.move - la instalacion puede requerir reinicio u otros deps." -ForegroundColor Yellow
}
