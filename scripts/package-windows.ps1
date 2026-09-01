$ErrorActionPreference = "Stop"

mvn clean package

if ($LASTEXITCODE -ne 0) {
    throw "Maven build basarisiz oldu."
}

Remove-Item -Recurse -Force release/windows -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Force -Path release/windows | Out-Null

$jar = Get-ChildItem target/*.jar |
    Where-Object {
        $_.Name -notlike "*sources*" -and
        $_.Name -notlike "*javadoc*"
    } |
    Select-Object -First 1

if ($null -eq $jar) {
    throw "JAR dosyasi bulunamadi."
}

Write-Host "JAR: $($jar.Name)"

jpackage `
    --type exe `
    --name AeroGamecenter `
    --input target `
    --main-jar $jar.Name `
    --dest release/windows

if ($LASTEXITCODE -ne 0) {
    throw "jpackage basarisiz oldu."
}

$exe = Get-ChildItem release/windows/*.exe |
    Select-Object -First 1

if ($null -eq $exe) {
    throw "EXE dosyasi olusturulamadi."
}

$hash = Get-FileHash $exe.FullName -Algorithm SHA256

"$($hash.Hash.ToLower())  $($exe.Name)" |
    Out-File "$($exe.FullName).sha256" -Encoding ascii

Write-Host "EXE OLUSTU: $($exe.FullName)"
