$ErrorActionPreference = "Stop"

Write-Host "Mevcut dizin:"
Get-Location

Write-Host "Tum dosyalar:"
Get-ChildItem -Recurse | Select-Object FullName

Write-Host "POM kontrolu:"
Get-ChildItem pom.xml

Write-Host "Maven build basliyor..."

mvn -f Gamecenter/AeroGamecenter/pom.xml clean package

Write-Host "Maven build bitti."

Write-Host "Target var mi?"
Test-Path target

if (Test-Path target) {
    Get-ChildItem target
} else {
    Write-Host "TARGET KLASORU BULUNAMADI!"
    exit 1
}
