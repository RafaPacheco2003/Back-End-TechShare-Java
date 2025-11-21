<#
Test script para autenticar y llamar endpoints protegidos.

Uso:
  powershell -NoProfile -ExecutionPolicy Bypass -File "./scripts/test-login.ps1" -BaseUrl "http://localhost:8080" -Email "admin@system.com" -Password "password"

Lo que hace:
  - Hace POST a /login con JSON {email,password}
  - Extrae el header Authorization (Bearer token)
  - Decodifica la parte payload del JWT (base64url -> JSON)
  - Hace GET a /user/info con el token en Authorization
  - Imprime status y body
#>

param(
    [string]$BaseUrl = 'http://localhost:8080',
    [string]$Email = 'admin@system.com',
    [string]$Password = 'password'
)

try {
    $loginUrl = "$BaseUrl/login"
    Write-Host "POST $loginUrl with $Email"

    $body = @{ email = $Email; password = $Password } | ConvertTo-Json
    $resp = Invoke-WebRequest -Uri $loginUrl -Method Post -ContentType 'application/json' -Body $body -UseBasicParsing -ErrorAction Stop

    $authHeader = $resp.Headers['Authorization']
    if (-not $authHeader) {
        Write-Error "No Authorization header in response. Status: $($resp.StatusCode)"
        exit 1
    }

    $token = $authHeader -replace '^Bearer\s+', ''
    Write-Host "TOKEN: $token"

    # Decodificar payload (base64url)
    $parts = $token -split '\.'
    if ($parts.Length -lt 2) {
        Write-Error "Token mal formado"
        exit 1
    }
    $payload = $parts[1]

    # Convertir base64url -> base64
    $pad = (4 - ($payload.Length % 4)) % 4
    $payload += '=' * $pad
    $payload = $payload.Replace('-', '+').Replace('_', '/')

    $bytes = [System.Convert]::FromBase64String($payload)
    $payloadJson = [System.Text.Encoding]::UTF8.GetString($bytes)
    Write-Host "PAYLOAD:`n$payloadJson"

    # Llamar endpoint protegido
    $headers = @{ Authorization = "Bearer $token" }
    $infoUrl = "$BaseUrl/user/info"
    Write-Host "GET $infoUrl"
    $infoResp = Invoke-WebRequest -Uri $infoUrl -Headers $headers -UseBasicParsing -ErrorAction Stop

    Write-Host "STATUS:$($infoResp.StatusCode)"
    Write-Host "BODY:`n$($infoResp.Content)"
} catch {
    Write-Error "Error: $($_.Exception.Message)"
    if ($_.InvocationInfo) { Write-Host $_.InvocationInfo.PositionMessage }
    exit 1
}
