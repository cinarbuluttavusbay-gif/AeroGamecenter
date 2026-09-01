
$ErrorActionPreference = "Stop"

# Maven ile projeyi derle
mvn clean package

Write-Host "Proje dosyalari:"
Get-ChildItem

Write-Host "Target klasoru:"
Get-ChildItem target

# Eski release klasörünü temizle
Remove-Item -Recurse -Force release/windows -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Force -Path release/windows | Out-Null

# JAR dosyasını bul
$jar = Get-ChildItem target/*.jar |
    Where-Object { $_.Name -notlike "*sources*" -and $_.Name -notlike "*javadoc*" } |
    Select-Object -First 1

if ($null -eq $jar) {
    throw "JAR dosyası bulunamadı."
}

# EXE oluştur
jpackage `
    --type exe `
    --name Gamecenter `
    --input target `
    --main-jar $jar.Name `
    --dest release/windows

# SHA256 oluştur
$exe = Get-ChildItem release/windows/*.exe | Select-Object -First 1

if ($null -eq $exe) {
    throw "EXE dosyası oluşturulamadı."
}

$hash = Get-FileHash $exe.FullName -Algorithm SHA256

"$($hash.Hash.ToLower())  $($exe.Name)" |
    Out-File "$($exe.FullName).sha256" -Encoding ascii
```

