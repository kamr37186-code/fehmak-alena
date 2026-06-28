$ErrorActionPreference = "Continue"

$RootDir = Split-Path -Parent -Path $PSScriptRoot
Set-Location -Path $RootDir

Write-Host "Starting Spring Boot backend in a new window..."
Start-Process -FilePath "cmd.exe" -ArgumentList "/c title Spring Boot Backend && cd backend && .\mvnw.cmd spring-boot:run"

Write-Host "Waiting 35 seconds for the backend server to fully start..."
Start-Sleep -Seconds 35

Write-Host "Running the data seeding script..."

cd scripts
node seed_database.js

Write-Host "Done! Please check the output above."
