# End-to-end smoke test for the Enigma REST API.
# Run run-server.bat first (in its own window) and wait for "Started EnigmaApplication",
# then run this script in a separate PowerShell window:
#   powershell -ExecutionPolicy Bypass -File test-api.ps1

$ErrorActionPreference = "Stop"
$base = "http://localhost:8080/enigma"
$xmlPath = Join-Path $PSScriptRoot "enigma-logic\enigma-loader\src\main\resources\ex3-sanity-small.xml"

function Section($title) {
    Write-Host ""
    Write-Host "==== $title ====" -ForegroundColor Cyan
}

Section "1. Load machine definition from XML ($xmlPath)"
$loadRaw = curl.exe -s -X POST "$base/load" -F "file=@$xmlPath"
Write-Host $loadRaw
$loadResp = $loadRaw | ConvertFrom-Json
if (-not $loadResp.success) {
    if ($loadResp.error -like "*already exists*") {
        Write-Host "(Machine already loaded from a previous run - continuing with existing one)" -ForegroundColor Yellow
        $machineName = "sanity"
    } else {
        throw "Load failed: $($loadResp.error)"
    }
} else {
    $machineName = $loadResp.name
}
Write-Host "Machine name: $machineName" -ForegroundColor Green

Section "2. Create a session for machine '$machineName'"
$sessionResp = Invoke-RestMethod -Method Post -Uri "$base/session" `
    -ContentType "application/json" -Body (@{ machine = $machineName } | ConvertTo-Json)
$sessionId = $sessionResp.sessionId
Write-Host "Session ID: $sessionId" -ForegroundColor Green

Section "3. Set automatic (random) code configuration"
$autoResp = Invoke-RestMethod -Method Put -Uri "$base/config/automatic?sessionID=$sessionId"
Write-Host $autoResp

Section "4. Check current machine status"
$statusResp = Invoke-RestMethod -Method Get -Uri "$base/config?sessionID=$sessionId&verbose=true"
$statusResp | ConvertTo-Json -Depth 5

Section "5. Encrypt a message (alphabet is A-F only, per the XML)"
$plaintext = "DEFACED"
$encryptResp = Invoke-RestMethod -Method Post -Uri "$base/process?sessionID=$sessionId&input=$plaintext"
Write-Host "Plaintext:  $plaintext"
Write-Host "Ciphertext: $($encryptResp.output)" -ForegroundColor Green
Write-Host "Rotor positions now: $($encryptResp.currentRotorsPositionCompact)"

Section "6. Reset rotors and decrypt the ciphertext back (proves reciprocity)"
Invoke-RestMethod -Method Put -Uri "$base/config/reset?sessionID=$sessionId" | Out-Null
$decryptResp = Invoke-RestMethod -Method Post -Uri "$base/process?sessionID=$sessionId&input=$($encryptResp.output)"
Write-Host "Ciphertext: $($encryptResp.output)"
Write-Host "Decrypted:  $($decryptResp.output)" -ForegroundColor Green
if ($decryptResp.output -eq $plaintext) {
    Write-Host ">>> SUCCESS: round trip matches the original plaintext!" -ForegroundColor Green
} else {
    Write-Host ">>> MISMATCH - check server logs" -ForegroundColor Red
}

Section "7. Fetch processing history for this session"
$historyResp = Invoke-RestMethod -Method Get -Uri "$base/history?sessionID=$sessionId"
$historyResp | ConvertTo-Json -Depth 5

Section "8. Clean up: delete the session"
Invoke-RestMethod -Method Delete -Uri "$base/session?sessionID=$sessionId" | Out-Null
Write-Host "Done." -ForegroundColor Cyan
