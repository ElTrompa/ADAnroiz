Param(
    [string]$url = 'http://localhost:8069',
    [string]$db = 'garantias',
    [string]$user = 'admin@example.com',
    [string]$pass = 'admin'
)

function rpc($endpoint, $params, $websession){
    $body = @{ jsonrpc = '2.0'; method = 'call'; params = $params } | ConvertTo-Json -Depth 10
    return Invoke-RestMethod -Uri "$url/$endpoint" -Method Post -Body $body -ContentType 'application/json' -WebSession $websession -ErrorAction Stop
}

Write-Host "Autenticando..."
$websession = New-Object Microsoft.PowerShell.Commands.WebRequestSession
$authBody = @{ db = $db; login = $user; password = $pass }
$authResp = rpc 'web/session/authenticate' $authBody $websession
if (-not $authResp.result) { Write-Host "Auth failed: $($authResp | ConvertTo-Json)"; exit 1 }
Write-Host "Autenticado. UID: $($authResp.result.uid)"

# Crear partner
$partnerBody = @{ jsonrpc = '2.0'; method = 'call'; params = @{ args = @( @( @{ name = 'Cliente Demo'; email = 'cliente@example.com' } ) ); kwargs = @{} } }
$partnerResp = Invoke-RestMethod -Uri "$url/web/dataset/call_kw/res.partner/create" -Method Post -Body ($partnerBody | ConvertTo-Json -Depth 10) -ContentType 'application/json' -WebSession $websession -ErrorAction Stop
$partnerId = $partnerResp.result
Write-Host "Partner creado: $partnerId"

# Crear producto
$productBody = @{ jsonrpc = '2.0'; method = 'call'; params = @{ args = @( @( @{ name = 'Bicicleta Demo'; list_price = 1000.0 } ) ); kwargs = @{} } }
$productResp = Invoke-RestMethod -Uri "$url/web/dataset/call_kw/product.product/create" -Method Post -Body ($productBody | ConvertTo-Json -Depth 10) -ContentType 'application/json' -WebSession $websession -ErrorAction Stop
$productId = $productResp.result
Write-Host "Product creado: $productId"

# Crear factura (account.move) con una línea
$lineValues = @{ name = 'Bicicleta Demo'; product_id = $productId; quantity = 1; price_unit = 1000 }
$moveValues = @{ move_type = 'out_invoice'; invoice_date = (Get-Date -Format yyyy-MM-dd); partner_id = $partnerId; invoice_line_ids = @( @(0,0,$lineValues) ) }
$moveBody = @{ jsonrpc = '2.0'; method = 'call'; params = @{ args = @( @( $moveValues ) ); kwargs = @{} } }
$moveResp = Invoke-RestMethod -Uri "$url/web/dataset/call_kw/account.move/create" -Method Post -Body ($moveBody | ConvertTo-Json -Depth 12) -ContentType 'application/json' -WebSession $websession -ErrorAction Stop
$moveId = $moveResp.result
Write-Host "Factura creada: $moveId"
