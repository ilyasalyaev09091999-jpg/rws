$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot
Set-Location $root

Write-Host "Building Java jars locally..."
& .\gradlew.bat `
  :archive-proto:build `
  :archive-api:bootJar `
  :refdata-api:bootJar `
  :route-api:bootJar `
  :rws-api:bootJar `
  :geography-importer:bootJar `
  -x test `
  --rerun-tasks `
  --no-daemon

Write-Host "Starting docker compose..."
docker compose up -d --build
