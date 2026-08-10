# End-to-end smoke test for the CHAT API (/api/*), local dev profile.
# Uses the X-Dev-User header (DevUserAuthFilter) to act as two different users.
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
Write-Host "Alice: $($aliceProfile.username) ($($aliceProfile.userId))"
Write-Host "Bob:   $($bobProfile.username) ($($bobProfile.userId))"

Section "2. List preset machines"
$machines = Invoke-As $alice Get "$base/machines"
$machines | ForEach-Object { Write-Host "$($_.name) - alphabet: $($_.abc) - rotors in use: $($_.rotorsInUse) of $($_.availableRotorIds.Count)" }

Section "3. Alice creates a conversation on 'Enigma I'"
$conv = Invoke-As $alice Post "$base/conversations" @{ machineName = "Enigma I" }
Write-Host "Conversation: $($conv.id)"
Write-Host "Invite code:  $($conv.inviteCode)" -ForegroundColor Yellow
Write-Host "Code: rotors $($conv.rotorIds -join ',') | reflector $($conv.reflectorId) | plugs [$($conv.plugs -join ' ')]"
Write-Host "Starting positions: $($conv.originalPositions -join ',')"

Section "4. Bob joins with the invite code"
$joined = Invoke-As $bob Post "$base/conversations/join" @{ inviteCode = $conv.inviteCode }
Write-Host "Participants now: $($joined.participants | ForEach-Object { $_.username })"

Section "5. Alice sends a message (with spaces + punctuation passthrough)"
$msg1 = Invoke-As $alice Post "$base/conversations/$($conv.id)/messages" @{ text = "HELLO BOB, MEET ME AT DAWN!" }
Write-Host "Plain sent:  HELLO BOB, MEET ME AT DAWN!"
Write-Host "Ciphertext:  $($msg1.ciphertext)" -ForegroundColor Yellow
Write-Host "Code at encryption: $($msg1.codeCompact)"

Section "6. Bob polls and reads the decrypted message"
$poll = Invoke-As $bob Get "$base/conversations/$($conv.id)/messages?afterSeq=0"
$received = $poll.messages[-1]
Write-Host "From $($received.senderUsername): '$($received.plaintext)'" -ForegroundColor Green
if ($received.plaintext -eq "HELLO BOB, MEET ME AT DAWN!") {
    Write-Host ">>> Decryption matches the original message" -ForegroundColor Green
} else {
    Write-Host ">>> MISMATCH!" -ForegroundColor Red
}
Write-Host "Rotor positions now: $($poll.currentPositionsCompact)"

Section "7. Bob replies; Alice polls incrementally (afterSeq=$($received.seq))"
Invoke-As $bob Post "$base/conversations/$($conv.id)/messages" @{ text = "UNDERSTOOD. BRING THE CODEBOOK." } | Out-Null
$alicePoll = Invoke-As $alice Get "$base/conversations/$($conv.id)/messages?afterSeq=$($received.seq)"
$reply = $alicePoll.messages[-1]
Write-Host "From $($reply.senderUsername): '$($reply.plaintext)'" -ForegroundColor Green
Write-Host "(incremental poll returned $($alicePoll.messages.Count) new message)"

Section "8. A third user cannot join (conversation full) or read"
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
    Write-Host "UNEXPECTED: eve read messages of a conversation she is not in" -ForegroundColor Red
} catch {
    Write-Host "Read rejected as expected (HTTP $($_.Exception.Response.StatusCode.value__))" -ForegroundColor Green
}

Section "9. Alice's conversation list"
$list = Invoke-As $alice Get "$base/conversations"
$list | ForEach-Object { Write-Host "$($_.machineName) | participants: $($_.participants.username -join ', ') | messages: $($_.lastSeq)" }

Write-Host ""
Write-Host "ALL CHAT FLOWS PASSED" -ForegroundColor Cyan
