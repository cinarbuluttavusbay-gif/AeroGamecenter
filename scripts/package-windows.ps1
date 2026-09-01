$ErrorActionPreference = "Stop"

Write-Host "[INFO] Maven build basliyor..."

mvn clean package

if ($LASTEXITCODE -ne 0) {
    throw "Maven build basarisiz oldu."
}

Write-Host "[INFO] Maven build tamamlandi."

Write-Host "[INFO] JAR dosyasi araniyor..."

$jar = Get-ChildItem -Path "target" -Filter "*.jar" -File |
    Where-Object {
        $_.Name -notlike "*sources*" -and
        $_.Name -notlike "*javadoc*"
    } |
    Select-Object -First 1

if ($null -eq $jar) {
    throw "target klasorunde JAR bulunamadi."
}

Write-Host "[INFO] JAR bulundu: $($jar.FullName)"

Write-Host "[INFO] Windows release klasoru hazirlaniyor..."

Remove-Item -Recurse -Force "release/windows" -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Force -Path "release/windows" | Out-Null

Write-Host "[INFO] jpackage calistiriliyor..."

jpackage `
    --type exe `
    --name "AeroGamecenter" `
    --input "target" `
    --main-jar "$($jar.Name)" `
    --main-class "RobloxTrackerApp" `
    --dest "release/windows" `
    --win-dir-chooser `
    --win-menu `
    --win-shortcut

if ($LASTEXITCODE -ne 0) {
    throw "jpackage basarisiz oldu."
}

Write-Host "[INFO] jpackage tamamlandi."

$exe = Get-ChildItem -Path "release/windows" -Filter "*.exe" -File |
    Select-Object -First 1

if ($null -eq $exe) {
    throw "EXE dosyasi olusturulamadi."
}

Write-Host "[INFO] EXE OLUSTU: $($exe.FullName)"

Write-Host "[INFO] SHA256 hesaplaniyor..."

$hash = Get-FileHash -Path $exe.FullName -Algorithm SHA256

"$($hash.Hash.ToLower())  $($exe.Name)" |
    Out-File -FilePath "$($exe.FullName).sha256" -Encoding ascii

Write-Host "[INFO] SHA256 olusturuldu."
Write-Host "[INFO] Windows paketi basariyla tamamlandi."

Write-Host ""
Write-Host "========================================"
Write-Host " EXE: $($exe.FullName)"
Write-Host " SHA: $($exe.FullName).sha256"
Write-Host "========================================"
