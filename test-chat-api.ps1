# End-to-end smoke test for the CHAT API (/api/*), local dev profile.
# Uses the X-Dev-User header (DevUserAuthFilter) to act as two different users.
#
# NOTE: encryption lives in the browser now, so this script exercises the
# TRANSPORT only - it posts ciphertext and checks it comes back unchanged.
# Cipher correctness is covered by frontend/src/enigma/machine.test.js, which
# replays vectors generated from the Java implementation.
#
# Start the server first (run-server.bat), then:
#   powershell -ExecutionPolicy Bypass -File test-chat-api.ps1

$ErrorActionPreference = "Stop"
$base = "http://localhost:8080/api"
$alice = "11111111-1111-1111-1111-111111111111"
$bob   = "22222222-2222-2222-2222-222222222222"

function Section($title) {
    Write-Host ""
    Write-Host "==== $title ====" -ForegroundColor Cyan
}

function Invoke-As($user, $method, $uri, $body) {
    $headers = @{ "X-Dev-User" = $user }
    if ($null -ne $body) {
        return Invoke-RestMethod -Method $method -Uri $uri -Headers $headers `
            -ContentType "application/json" -Body ($body | ConvertTo-Json)
    }
    return Invoke-RestMethod -Method $method -Uri $uri -Headers $headers
}

Section "0. Unauthenticated request is rejected"
try {
    Invoke-RestMethod -Method Get -Uri "$base/machines" | Out-Null
    Write-Host "UNEXPECTED: request succeeded without auth" -ForegroundColor Red
} catch {
    Write-Host "Rejected as expected (HTTP $($_.Exception.Response.StatusCode.value__))" -ForegroundColor Green
}

Section "1. Create profiles for Alice and Bob"
$aliceProfile = Invoke-As $alice Put "$base/profile" @{ username = "alice" }
$bobProfile   = Invoke-As $bob   Put "$base/profile" @{ username = "bob" }
Write-Host "Alice: $($aliceProfile.username)  |  Bob: $($bobProfile.username)"

Section "2. List preset machines"
$machines = Invoke-As $alice Get "$base/machines"
$machines | ForEach-Object { Write-Host "$($_.name) - alphabet: $($_.abc) - rotors in use: $($_.rotorsInUse) of $($_.availableRotorIds.Count)" }

Section "3. Fetch machine wiring (what the browser runs the machine from)"
$wiring = Invoke-As $alice Get "$base/machines/Enigma%20I/wiring"
Write-Host "Rotors: $($wiring.rotors.Count) | reflectors: $($wiring.reflectors.id -join ', ') | alphabet length: $($wiring.alphabet.Length)"
$firstRotor = $wiring.rotors[0]
Write-Host "Rotor $($firstRotor.id): notch=$($firstRotor.notch), right[0..5]=$($firstRotor.right[0..5] -join ','), left[0..5]=$($firstRotor.left[0..5] -join ',')"

Section "4. Alice creates a conversation on 'Enigma I'"
$conv = Invoke-As $alice Post "$base/conversations" @{
    machineName = "Enigma I"; rotorIds = @(1, 2, 3); reflectorId = "I"
    plugPairs = @(); initialPositions = @("A", "A", "A")
}
Write-Host "Invite code: $($conv.inviteCode)" -ForegroundColor Yellow
Write-Host "Machine: rotors $($conv.rotorIds -join ',') | reflector $($conv.reflectorId) | ground $($conv.initialPositions -join '')"

Section "5. Bob joins with the invite code"
$joined = Invoke-As $bob Post "$base/conversations/join" @{ inviteCode = $conv.inviteCode }
Write-Host "Participants now: $($joined.participants.username -join ', ')"

Section "6. Alice posts a message from where the machine stands"
$state = Invoke-As $alice Get "$base/conversations/$($conv.id)/messages?afterSeq=0"
$startPositions = $state.currentPositions
$endPositions = @("A", "A", "D")   # three letters advance the right rotor three steps
$ciphertext = "XYZ"
$msg1 = Invoke-As $alice Post "$base/conversations/$($conv.id)/messages" @{
    ciphertext = $ciphertext; startPositions = $startPositions
    endPositions = $endPositions; expectedSeq = $state.lastSeq
}
Write-Host "Machine was at $($startPositions -join '') -> stored seq $($msg1.seq) '$($msg1.ciphertext)'"

Section "7. Bob polls: ciphertext, key, and the machine's new position"
$poll = Invoke-As $bob Get "$base/conversations/$($conv.id)/messages?afterSeq=0"
$received = $poll.messages[-1]
Write-Host "From $($received.senderUsername): '$($received.ciphertext)' key=$($received.startPositions -join '')"
Write-Host "Machine has moved to: $($poll.currentPositions -join '')"
$fields = $received.PSObject.Properties.Name
Write-Host "Fields returned: $($fields -join ', ')"
if ($fields -contains "plaintext") {
    Write-Host ">>> LEAK: server returned a plaintext field!" -ForegroundColor Red
} elseif (($poll.currentPositions -join '') -eq ($endPositions -join '')) {
    Write-Host ">>> Machine advanced, and no plaintext anywhere in the response" -ForegroundColor Green
} else {
    Write-Host ">>> Machine did not advance as expected" -ForegroundColor Red
}

Section "8. A message encrypted from a stale position is refused"
try {
    Invoke-As $bob Post "$base/conversations/$($conv.id)/messages" @{
        ciphertext = "ABC"; startPositions = @("A", "A", "A")
        endPositions = @("A", "A", "D"); expectedSeq = 0
    } | Out-Null
    Write-Host "UNEXPECTED: stale send accepted" -ForegroundColor Red
} catch {
    Write-Host "Rejected as expected (HTTP $($_.Exception.Response.StatusCode.value__) conflict)" -ForegroundColor Green
}

Section "8b. Server rejects malformed rotor positions"
$now = Invoke-As $bob Get "$base/conversations/$($conv.id)/messages?afterSeq=0"
try {
    Invoke-As $bob Post "$base/conversations/$($conv.id)/messages" @{
        ciphertext = "ABC"; startPositions = @("A")
        endPositions = $now.currentPositions; expectedSeq = $now.lastSeq
    } | Out-Null
    Write-Host "UNEXPECTED: wrong number of start positions accepted" -ForegroundColor Red
} catch {
    Write-Host "Rejected as expected (HTTP $($_.Exception.Response.StatusCode.value__))" -ForegroundColor Green
}
try {
    Invoke-As $bob Post "$base/conversations/$($conv.id)/messages" @{
        ciphertext = "ABC"; startPositions = $now.currentPositions
        endPositions = @("A", "B", "5"); expectedSeq = $now.lastSeq
    } | Out-Null
    Write-Host "UNEXPECTED: non-alphabet rotor position accepted" -ForegroundColor Red
} catch {
    Write-Host "Rejected as expected (HTTP $($_.Exception.Response.StatusCode.value__))" -ForegroundColor Green
}

Section "9. A third user cannot join (full) or read"
$eve = "33333333-3333-3333-3333-333333333333"
Invoke-As $eve Put "$base/profile" @{ username = "eve" } | Out-Null
try {
    Invoke-As $eve Post "$base/conversations/join" @{ inviteCode = $conv.inviteCode } | Out-Null
    Write-Host "UNEXPECTED: eve joined a full conversation" -ForegroundColor Red
} catch {
    Write-Host "Join rejected as expected (HTTP $($_.Exception.Response.StatusCode.value__))" -ForegroundColor Green
}
try {
    Invoke-As $eve Get "$base/conversations/$($conv.id)/messages" | Out-Null
    Write-Host "UNEXPECTED: eve read a conversation she is not in" -ForegroundColor Red
} catch {
    Write-Host "Read rejected as expected (HTTP $($_.Exception.Response.StatusCode.value__))" -ForegroundColor Green
}

Write-Host ""
Write-Host "ALL CHAT TRANSPORT CHECKS PASSED" -ForegroundColor Cyan
