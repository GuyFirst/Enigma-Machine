# Regenerates frontend/src/enigma/vectors.json from the Java implementation.
# Run this after changing anything in enigma-machine / enigma-engine, then
# `npm test` in frontend/ to confirm the browser port still agrees.
#
#   powershell -ExecutionPolicy Bypass -File scripts/generate-vectors.ps1

$ErrorActionPreference = "Stop"
$proj = Split-Path $PSScriptRoot -Parent
$jdk = "C:\Program Files\Eclipse Adoptium\jdk-21.0.12.8-hotspot"
$mvn = "C:\Users\guyfi\tools\apache-maven-3.9.16\bin"
$env:JAVA_HOME = $jdk
$env:PATH = "$jdk\bin;$mvn;$env:PATH"

$work = Join-Path $env:TEMP "enigma-vectors"
New-Item -ItemType Directory -Force -Path $work | Out-Null

Write-Host "Building modules…" -ForegroundColor Cyan
Push-Location $proj
mvn -q -pl enigma-logic/enigma-loader -am install -DskipTests | Out-Null
Pop-Location

Push-Location (Join-Path $proj "enigma-logic\enigma-loader")
mvn -q "dependency:build-classpath" "-Dmdep.outputFile=$work\cp.txt" | Out-Null
Pop-Location

$cp = (Get-Content "$work\cp.txt") + ";" + `
      "$proj\enigma-logic\enigma-machine\target\classes;" + `
      "$proj\enigma-logic\enigma-engine\target\classes;" + `
      "$proj\enigma-logic\enigma-loader\target\classes"

Write-Host "Compiling generator…" -ForegroundColor Cyan
javac -nowarn -proc:none -cp "$cp" -d "$work\classes" "$PSScriptRoot\VectorGen.java"

Write-Host "Generating vectors…" -ForegroundColor Cyan
java -cp "$cp;$work\classes" VectorGen `
    "$proj\enigma-app\src\main\resources\preset-machines\enigma-i.xml" `
    "$proj\enigma-app\src\main\resources\preset-machines\sanity-small.xml" `
    "$proj\frontend\src\enigma\vectors.json"

Write-Host "Done. Now run: cd frontend; npm test" -ForegroundColor Green
