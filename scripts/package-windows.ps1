$ErrorActionPreference = "Stop"

Write-Host "========================================"
Write-Host " AeroGamecenter Windows Package"
Write-Host "========================================"

# 1. Maven build
Write-Host "[1/5] Maven build basliyor..."

mvn clean package

if ($LASTEXITCODE -ne 0) {
    throw "Maven build basarisiz oldu."
}

# 2. JAR bul
Write-Host "[2/5] JAR dosyasi bulunuyor..."

$jar = Get-ChildItem "target" -Filter "*.jar" -File |
    Where-Object {
        $_.Name -notlike "*sources*" -and
        $_.Name -notlike "*javadoc*"
    } |
    Select-Object -First 1

if ($null -eq $jar) {
    throw "target klasorunde JAR bulunamadi."
}

Write-Host "JAR: $($jar.FullName)"

# 3. Release klasoru
Write-Host "[3/5] Release klasoru hazirlaniyor..."

Remove-Item "release/windows" -Recurse -Force -ErrorAction SilentlyContinue
New-Item "release/windows" -ItemType Directory -Force | Out-Null

# 4. Windows installer
Write-Host "[4/5] Windows installer olusturuluyor..."

jpackage `
    --type exe `
    --name "AeroGamecenter" `
    --input "target" `
    --main-jar "$($jar.Name)" `
    --main-class "RobloxTrackerApp" `
    --dest "release/windows"

if ($LASTEXITCODE -ne 0) {
    throw "jpackage Windows installer olusturamadi."
}

# 5. EXE kontrolu ve SHA256
Write-Host "[5/5] EXE kontrol ediliyor..."

$exe = Get-ChildItem "release/windows" -Filter "*.exe" -File |
    Select-Object -First 1

if ($null -eq $exe) {
    throw "EXE dosyasi bulunamadi."
}

Write-Host "EXE OLUSTU: $($exe.FullName)"

$hash = Get-FileHash $exe.FullName -Algorithm SHA256

"$($hash.Hash.ToLower())  $($exe.Name)" |
    Out-File "$($exe.FullName).sha256" -Encoding ascii

Write-Host "SHA256 OLUSTU: $($exe.FullName).sha256"

Write-Host "========================================"
Write-Host " Windows package BASARILI"
Write-Host "========================================"
