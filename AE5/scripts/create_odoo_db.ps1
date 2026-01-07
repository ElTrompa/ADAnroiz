Param(
    [string]$url = 'http://localhost:8069',
    [string]$master = 'admin',
    [string]$db = 'garantias',
    [string]$adminEmail = 'admin@example.com',
    [string]$adminPass = 'admin',
    [bool]$demo = $true,
    [string]$lang = 'es_ES'
)

Write-Host "Comprobando existencia de la BD '$db' en $url..."
$body = @{ jsonrpc = '2.0'; method = 'call'; params = @{} } | ConvertTo-Json -Depth 5
$listResp = Invoke-RestMethod -Uri "$url/web/database/list" -Method Post -Body $body -ContentType 'application/json' -ErrorAction SilentlyContinue
if ($listResp -and $listResp.result -contains $db) {
    Write-Host "La base de datos '$db' ya existe."
    exit 0
}

Write-Host "Creando base de datos '$db'..."
$createParams = @{ master_pwd = $master; name = $db; demo = $demo; lang = $lang; admin_password = $adminPass; admin_email = $adminEmail }
$createBody = @{ jsonrpc = '2.0'; method = 'call'; params = $createParams } | ConvertTo-Json -Depth 5
$resp = Invoke-RestMethod -Uri "$url/web/database/create" -Method Post -Body $createBody -ContentType 'application/json' -ErrorAction SilentlyContinue
if ($resp -and $resp.result) {
    Write-Host "Solicitud enviada. Revisa los logs de Odoo si hace falta."
} else {
    Write-Host "Error al crear la base de datos:" -ForegroundColor Red
    Write-Host ($resp | ConvertTo-Json -Depth 5)
    exit 1
}
