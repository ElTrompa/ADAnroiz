Param(
    [string]$url = 'http://localhost:8069',
    [string]$db = 'garantias',
    [string]$user = 'admin@example.com',
    [string]$pass = 'admin',
    [int]$mesesGarantia = 12
)

function rpc_call($endpoint, $params, $websession){
    $body = @{ jsonrpc = '2.0'; method = 'call'; params = $params }
    $json = $body | ConvertTo-Json -Depth 12
    return Invoke-RestMethod -Uri "$url/$endpoint" -Method Post -Body $json -ContentType 'application/json' -WebSession $websession -ErrorAction Stop
}

Write-Host "Autenticando en Odoo $url (db=$db)"
$w = New-Object Microsoft.PowerShell.Commands.WebRequestSession
$authParams = @{ db = $db; login = $user; password = $pass }
$auth = rpc_call 'web/session/authenticate' $authParams $w
if (-not $auth.result) { Write-Host "No auth"; exit 1 }
Write-Host "Auth OK uid: $($auth.result.uid)"

# Obtener facturas venta
$domain = @(@('move_type','=', 'out_invoice'))
$fields = @('id','name','partner_id','invoice_date','amount_total','invoice_line_ids')
$searchParams = @{ model = 'account.move'; method = 'search_read'; args = @(@('move_type','=', 'out_invoice'), $fields); kwargs = @{ limit = 100 } }
$resp = rpc_call 'web/dataset/call_kw/account.move/search_read' $searchParams $w
Write-Host "Facturas result: $($resp | ConvertTo-Json -Depth 6)"

if (-not $resp.result -or -not $resp.result.records) { Write-Host "No invoices found"; exit 0 }
$invoices = $resp.result.records

$created = 0; $skipped = 0
foreach ($inv in $invoices) {
    $invId = $inv.id
    $invName = $inv.name
    $cliente = ''
    if ($inv.partner_id -and $inv.partner_id.Count -ge 2) { $cliente = $inv.partner_id[1] }
    $lineIds = $inv.invoice_line_ids
    if (-not $lineIds) { continue }

    # Leer líneas
    $fieldsLine = @('id','product_id','quantity','name')
$linesParams = @{ model = 'account.move.line'; method = 'read'; args = @(@($lineIds), @($fieldsLine)); kwargs = @{ } }
    $linesResp = rpc_call 'web/dataset/call_kw/account.move.line/read' $linesParams $w
    $lines = $linesResp.result.records
    foreach ($l in $lines) {
        $lineId = $l.id
        # comprobar en MongoDB
        $check = docker exec garantias_mongodb mongosh --username admin --password admin_password --quiet --eval "db.getSiblingDB('garantias_db').warranties.findOne({lineaFacturaId:$lineId})"
        if ($check -and $check -ne 'null') {
            Write-Host "Skipping existing warranty for line $lineId"
            $skipped++
            continue
        }
        $prodId = $null; $prodName = $null
        if ($l.product_id -and $l.product_id.Count -ge 2) { $prodId = $l.product_id[0]; $prodName = $l.product_id[1] }
        $fecha = (Get-Date).ToString('yyyy-MM-dd')
        $fechaFin = (Get-Date).AddMonths($mesesGarantia).ToString('yyyy-MM-dd')
        $doc = @{ facturaId = $invId; nombreFactura = $invName; lineaFacturaId = $lineId; productoId = $prodId; nombreProducto = $prodName; nombreCliente = $cliente; fechaCompra = $fecha; fechaInicioGarantia = $fecha; fechaFinGarantia = $fechaFin; mesesGarantia = $mesesGarantia; estado = 'ACTIVA'; numeroSerie = $null }
        $jsonDoc = $doc | ConvertTo-Json -Depth 6
        Write-Host "Inserting warranty for line $lineId"
        docker exec garantias_mongodb mongosh --username admin --password admin_password --quiet --eval "db.getSiblingDB('garantias_db').warranties.insertOne($jsonDoc)"
        $created++
    }
}
Write-Host "Done. created=$created skipped=$skipped"