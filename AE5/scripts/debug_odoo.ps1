# Debug Odoo: listar BDs, autenticar y listar facturas (search_read)
Param(
    [string]$url = 'http://localhost:8069',
    [string]$db = 'garantias',
    [string]$user = 'admin',
    [string]$pass = 'admin'
)

Write-Host "URL: $url, DB: $db, user: $user"

# List DBs
$listBody = @{ jsonrpc = '2.0'; method = 'call'; params = @{} } | ConvertTo-Json -Depth 5
Try {
    $listResp = Invoke-RestMethod -Uri "$url/web/database/list" -Method Post -Body $listBody -ContentType 'application/json' -ErrorAction Stop
    Write-Host "Databases:"
    $listResp.result | ForEach-Object { Write-Host " - $_" }
} Catch {
    Write-Host "Error listing DBs: $_" -ForegroundColor Red
}

# Authenticate
$authBody = @{ jsonrpc = '2.0'; method = 'call'; params = @{ db = $db; login = $user; password = $pass } } | ConvertTo-Json -Depth 6
try {
    $websession = New-Object Microsoft.PowerShell.Commands.WebRequestSession
    $authResp = Invoke-RestMethod -Uri "$url/web/session/authenticate" -Method Post -Body $authBody -ContentType 'application/json' -WebSession $websession -ErrorAction Stop
    Write-Host "Auth result: " ($authResp | ConvertTo-Json -Depth 5)
} catch {
    Write-Host "Auth error: $_" -ForegroundColor Red
    exit 1
}

# Search_read invoices
$searchBody = @{ jsonrpc = '2.0'; method = 'call'; params = @{ model = 'account.move'; method = 'search_read'; args = @( @(@(@('move_type','=', 'out_invoice'))) ); kwargs = @{ fields = @('id','name','partner_id','invoice_date','amount_total','invoice_line_ids') } } } | ConvertTo-Json -Depth 10
try {
    $invoicesResp = Invoke-RestMethod -Uri "$url/web/dataset/call_kw/account.move/search_read" -Method Post -Body $searchBody -ContentType 'application/json' -WebSession $websession -ErrorAction Stop
    Write-Host "Invoices result count: " $invoicesResp.result.records.Count
    $invoicesResp.result.records | ForEach-Object { Write-Host "- invoice:" $_.name "date:" $_.invoice_date "partner:" (($_.partner_id -join ' ')) }
} catch {
    Write-Host "Invoice search error: $_" -ForegroundColor Yellow
}

# Search_read partners
$partnerSearch = @{ jsonrpc = '2.0'; method = 'call'; params = @{ model = 'res.partner'; method = 'search_read'; args = @( @() ); kwargs = @{ fields = @('id','name','email'); 'limit' = 10 } } } | ConvertTo-Json -Depth 10
try {
    $pResp = Invoke-RestMethod -Uri "$url/web/dataset/call_kw/res.partner/search_read" -Method Post -Body $partnerSearch -ContentType 'application/json' -WebSession $websession -ErrorAction Stop
    Write-Host "Partners found: " $pResp.result.records.Count
    Write-Host "Raw partners JSON:"; $pResp | ConvertTo-Json -Depth 10
} catch {
    Write-Host "Partner search error: $_" -ForegroundColor Yellow
}

# Search_read products
$productSearch = @{ jsonrpc = '2.0'; method = 'call'; params = @{ model = 'product.product'; method = 'search_read'; args = @( @() ); kwargs = @{ fields = @('id','name'); 'limit' = 10 } } } | ConvertTo-Json -Depth 10
try {
    $prResp = Invoke-RestMethod -Uri "$url/web/dataset/call_kw/product.product/search_read" -Method Post -Body $productSearch -ContentType 'application/json' -WebSession $websession -ErrorAction Stop
    Write-Host "Products raw response:"; $prResp | ConvertTo-Json -Depth 10
} catch {
    Write-Host "Product search error: $_" -ForegroundColor Yellow
}
