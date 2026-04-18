$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot
Set-Location $root

Write-Host "Building Java jars locally..."
& .\gradlew.bat `
  :archive-api:bootJar `
  :refdata-api:bootJar `
  :route-api:bootJar `
  :rws-api:bootJar `
  :geography-importer:bootJar `
  -x test `
  --no-daemon

Write-Host "Starting docker compose..."
docker compose up -d --build
