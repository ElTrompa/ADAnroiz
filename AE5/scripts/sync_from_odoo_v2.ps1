Param(
    [string]$odooContainer = 'odoo_server',
    [string]$mongoContainer = 'garantias_mongodb',
    [string]$mongoDb = 'garantias_db',
    [int]$mesesGarantia = 12
)

Write-Host "Fetching invoices from Odoo container $odooContainer"
$command = "docker exec -i $odooContainer bash -lc \"odoo shell -d garantias -c /etc/odoo/odoo.conf << 'PY'\nwith odoo.registry('garantias').cursor() as cr:\n    env = odoo.api.Environment(cr, odoo.SUPERUSER_ID, {})\n    moves = env['account.move'].search_read([(\'move_type\',\'=\', \'out_invoice\')], ['id','name','partner_id','invoice_date','amount_total','invoice_line_ids'])\n    import json\n    print(json.dumps(moves, default=str))\nPY\""
$raw = cmd /c $command 2>&1 | Out-String
# The output contains logs then a JSON array line; try to extract the first line that starts with [
$jsonLine = ($raw -split "`n" | Where-Object { $_ -match '^\s*\[' } | Select-Object -First 1)
if (-not $jsonLine) {
    Write-Host "Could not parse Odoo output. Full output:\n$raw"
    exit 1
}
$moves = $jsonLine | ConvertFrom-Json

$created=0; $skipped=0
foreach ($m in $moves) {
    $invId = $m.id
    $invName = $m.name
    $cliente = ''
    if ($m.partner_id) { $cliente = $m.partner_id[1] }
    $lineIds = $m.invoice_line_ids
    if (-not $lineIds) { continue }
    foreach ($lid in $lineIds) {
        # check mongo
        $checkCmd = "docker exec $mongoContainer mongosh --username admin --password admin_password --quiet --eval \"db.getSiblingDB('$mongoDb').warranties.findOne({lineaFacturaId:$lid})\""
        $check = cmd /c $checkCmd 2>&1 | Out-String
        if ($check -and $check -match 'null') {
            # insert
            $fecha = (Get-Date).ToString('yyyy-MM-dd')
            $fechaFin = (Get-Date).AddMonths($mesesGarantia).ToString('yyyy-MM-dd')
            $doc = @{ facturaId = $invId; nombreFactura = $invName; lineaFacturaId = $lid; productoId = $null; nombreProducto = $null; nombreCliente = $cliente; fechaCompra = $fecha; fechaInicioGarantia = $fecha; fechaFinGarantia = $fechaFin; mesesGarantia = $mesesGarantia; estado = 'ACTIVA'; numeroSerie = $null }
            $jsonDoc = $doc | ConvertTo-Json -Depth 6
            $insertCmd = "docker exec $mongoContainer mongosh --username admin --password admin_password --quiet --eval \"db.getSiblingDB('$mongoDb').warranties.insertOne($jsonDoc)\""
            Write-Host "Inserting warranty for line $lid from invoice $invId"
            cmd /c $insertCmd | Out-Null
            $created++
        } else {
            Write-Host "Warranty exists for line $lid, skipping"
            $skipped++
        }
    }
}
Write-Host "Done. created=$created skipped=$skipped"