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

Write-Host "Autenticando..."
$websession = New-Object Microsoft.PowerShell.Commands.WebRequestSession
$authBody = @{ jsonrpc = '2.0'; method = 'call'; params = @{ db = $db; login = $user; password = $pass } }
$authResp = Invoke-RestMethod -Uri "$url/web/session/authenticate" -Method Post -Body ($authBody | ConvertTo-Json -Depth 6) -ContentType 'application/json' -WebSession $websession -ErrorAction Stop
if (-not $authResp.result) { Write-Host "Auth failed: $($authResp | ConvertTo-Json)"; exit 1 }
Write-Host "Autenticado. UID: $($authResp.result.uid)"

# Crear partner
$partnerResp = rpc_callkw 'res.partner' 'create' @( @{ name = 'Cliente Demo'; email = 'cliente@example.com' } ) $websession
$partnerId = $partnerResp.result
Write-Host "Partner creado: $partnerId"

# Crear producto
$productResp = rpc_callkw 'product.product' 'create' @( @{ name = 'Bicicleta Demo'; list_price = 1000.0 } ) $websession
$productId = $productResp.result
Write-Host "Product creado: $productId"

# Crear factura (account.move) con una línea
$lineValues = @{ name = 'Bicicleta Demo'; product_id = $productId; quantity = 1; price_unit = 1000 }
$moveValues = @{ move_type = 'out_invoice'; invoice_date = (Get-Date -Format yyyy-MM-dd); partner_id = $partnerId; invoice_line_ids = @( @(0,0,$lineValues) ) }
$moveResp = rpc_callkw 'account.move' 'create' @( $moveValues ) $websession
$moveId = $moveResp.result
Write-Host "Factura creada: $moveId"

# Publicar la factura (button_post)
if ($moveId) {
    $postResp = rpc_callkw 'account.move' 'action_post' @( @( $moveId ) ) $websession
    Write-Host "Post response:" ($postResp | ConvertTo-Json -Depth 10)
} else {
    Write-Host "No se pudo crear la factura; revisar respuesta anterior." -ForegroundColor Yellow
}
