$ErrorActionPreference = "Stop"

Write-Host "========================================"
Write-Host " AeroGamecenter Windows MSI Package"
Write-Host "========================================"

# 1. Maven build
Write-Host "[1/5] Maven build basliyor..."

mvn clean package

if ($LASTEXITCODE -ne 0) {
    throw "Maven build basarisiz oldu."
}

Write-Host "[OK] Maven build tamamlandi."

# 2. JAR dosyasini bul
Write-Host "[2/5] JAR dosyasi araniyor..."

$jar = Get-ChildItem -Path "target" -Filter "*.jar" -File |
    Where-Object {
        $_.Name -notlike "*sources*" -and
        $_.Name -notlike "*javadoc*"
    } |
    Select-Object -First 1

if ($null -eq $jar) {
    throw "target klasorunde JAR bulunamadi."
}

Write-Host "[OK] JAR: $($jar.Name)"

# 3. Release klasorunu temizle
Write-Host "[3/5] Release klasoru hazirlaniyor..."

Remove-Item `
    -Path "release/windows" `
    -Recurse `
    -Force `
    -ErrorAction SilentlyContinue

New-Item `
    -Path "release/windows" `
    -ItemType Directory `
    -Force | Out-Null

# 4. MSI olustur
Write-Host "[4/5] Windows MSI installer olusturuluyor..."

jpackage `
    --type msi `
    --name "AeroGamecenter" `
    --input "target" `
    --main-jar "$($jar.Name)" `
    --main-class "RobloxTrackerApp" `
    --dest "release/windows" `
    --win-shortcut `
    --win-menu

if ($LASTEXITCODE -ne 0) {
    throw "jpackage MSI olusturamadi."
}

Write-Host "[OK] jpackage tamamlandi."

# 5. MSI kontrolu ve SHA256
Write-Host "[5/5] MSI kontrol ediliyor..."

$msi = Get-ChildItem `
    -Path "release/windows" `
    -Filter "*.msi" `
    -File |
    Select-Object -First 1

if ($null -eq $msi) {
    throw "MSI dosyasi bulunamadi."
}

Write-Host "[OK] MSI OLUSTU: $($msi.FullName)"

$hash = Get-FileHash `
    -Path $msi.FullName `
    -Algorithm SHA256

"$($hash.Hash.ToLower())  $($msi.Name)" |
    Out-File `
        -FilePath "$($msi.FullName).sha256" `
        -Encoding ascii

Write-Host "[OK] SHA256 OLUSTU: $($msi.FullName).sha256"

Write-Host ""
Write-Host "========================================"
Write-Host " WINDOWS MSI PACKAGE BASARILI"
Write-Host "========================================"
Write-Host ""
Write-Host "MSI: $($msi.FullName)"
Write-Host ""
