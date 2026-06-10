$baseUrl = "http://localhost:8080/api/v1"

Write-Host "1. Testing Auth Login..."
$loginBody = @{
    email = "test@example.com"
    password = "password123"
} | ConvertTo-Json

$loginResponse = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method Post -Body $loginBody -ContentType "application/json"
$token = $loginResponse.token
Write-Host "Received Token: $(if ($token) { 'YES' } else { 'NO' })"

$headers = @{
    "Authorization" = "Bearer $token"
    "Content-Type" = "application/json"
}

Write-Host "2. Testing Create Automation..."
$createAutoBody = @{
    triggerKeyword = "PRICE"
    responseMessage = "Check out our pricing page!"
} | ConvertTo-Json

try {
    $autoResponse = Invoke-RestMethod -Uri "$baseUrl/automations" -Method Post -Headers $headers -Body $createAutoBody -ContentType "application/json"
    Write-Host "Created Automation ID: $($autoResponse.id)"
} catch {
    Write-Host "Error creating automation: $($_.Exception.Message)"
}

Write-Host "3. Testing Get Automations..."
try {
    $getAutoResponse = Invoke-RestMethod -Uri "$baseUrl/automations" -Method Get -Headers $headers
    Write-Host "Total Automations: $($getAutoResponse.Count)"
} catch {
    Write-Host "Error getting automations: $($_.Exception.Message)"
}

Write-Host "4. Testing Get Media (Instagram)..."
try {
    $mediaResponse = Invoke-RestMethod -Uri "$baseUrl/instagram/media" -Method Get -Headers $headers
    Write-Host "Media: $($mediaResponse)"
} catch {
    # It might fail if no account is connected, which is normal, but let's see the error
    Write-Host "Error getting media (expected if not connected): $($_.Exception.Message)"
}
