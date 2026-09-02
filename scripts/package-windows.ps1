$ErrorActionPreference = "Stop"

Write-Host "========================================"
Write-Host " AeroGamecenter Windows EXE Installer"
Write-Host "========================================"

# ============================================================
# 0. Java 21 kontrolu
# ============================================================

Write-Host "[0/5] Java kontrol ediliyor..."

$javaVersion = java -version 2>&1 | Select-Object -First 1
Write-Host "[INFO] $javaVersion"

# jpackage komutunun bulunup bulunmadigini kontrol et
$jpackageCommand = Get-Command jpackage -ErrorAction SilentlyContinue

if ($null -eq $jpackageCommand) {
    Write-Host "[WARN] jpackage PATH'te bulunamadi."

    # JAVA_HOME varsa kullan
    if ($env:JAVA_HOME) {
        $possibleJpackage = Join-Path $env:JAVA_HOME "bin\jpackage.exe"

        if (Test-Path $possibleJpackage) {
            $jpackageCommand = $possibleJpackage
            Write-Host "[OK] jpackage bulundu: $possibleJpackage"
        }
    }
}

# Yaygin Java 21 konumlarini kontrol et
if ($null -eq $jpackageCommand) {
    $possibleJdks = @(
        "C:\Program Files\Java\jdk-21",
        "C:\Program Files\Java\jdk-21.0.1",
        "C:\Program Files\Java\jdk-21.0.2",
        "C:\Program Files\Java\jdk-21.0.3",
        "C:\Program Files\Java\jdk-21.0.4",
        "C:\Program Files\Java\jdk-21.0.5",
        "C:\Program Files\Java\jdk-21.0.6",
        "C:\Program Files\Java\jdk-21.0.7",
        "C:\Program Files\Java\jdk-21.0.8"
    )

    foreach ($jdk in $possibleJdks) {
        $possibleJpackage = Join-Path $jdk "bin\jpackage.exe"

        if (Test-Path $possibleJpackage) {
            $jpackageCommand = $possibleJpackage
            Write-Host "[OK] jpackage bulundu: $possibleJpackage"
            break
        }
    }
}

if ($null -eq $jpackageCommand) {
    throw "jpackage bulunamadi. Java 21 JDK'nin PATH/JAVA_HOME ayarlarini kontrol edin."
}

# ============================================================
# 1. Maven build
# ============================================================

Write-Host ""
Write-Host "[1/5] Maven build basliyor..."

mvn clean package

if ($LASTEXITCODE -ne 0) {
    throw "Maven build basarisiz oldu."
}

Write-Host "[OK] Maven build tamamlandi."

# ============================================================
# 2. JAR dosyasini bul
# ============================================================

Write-Host ""
Write-Host "[2/5] JAR dosyasi araniyor..."

$jar = Get-ChildItem `
    -Path "target" `
    -Filter "*.jar" `
    -File |
    Where-Object {
        $_.Name -notlike "*sources*" -and
        $_.Name -notlike "*javadoc*"
    } |
    Sort-Object Length -Descending |
    Select-Object -First 1

if ($null -eq $jar) {
    throw "target klasorunde JAR bulunamadi."
}

Write-Host "[OK] JAR: $($jar.Name)"

# ============================================================
# 3. Release klasorunu temizle
# ============================================================

Write-Host ""
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

# ============================================================
# 4. Windows EXE installer olustur
# ============================================================

Write-Host ""
Write-Host "[4/5] Windows EXE installer olusturuluyor..."

& $jpackageCommand `
    --type exe `
    --name "AeroGamecenter" `
    --app-version "1.0.0" `
    --input "target" `
    --main-jar "$($jar.Name)" `
    --main-class "RobloxTrackerApp" `
    --dest "release/windows" `
    --win-dir-chooser `
    --win-shortcut `
    --win-menu `
    --win-menu-group "AeroGamecenter" `
    --win-per-user-install

if ($LASTEXITCODE -ne 0) {
    throw "jpackage EXE installer olusturamadi."
}

Write-Host "[OK] EXE installer olusturuldu."

# ============================================================
# 5. EXE kontrolu ve SHA256
# ============================================================

Write-Host ""
Write-Host "[5/5] EXE kontrol ediliyor..."

$installer = Get-ChildItem `
    -Path "release/windows" `
    -Filter "*.exe" `
    -File |
    Select-Object -First 1

if ($null -eq $installer) {
    throw "EXE installer bulunamadi."
}

Write-Host "[OK] INSTALLER OLUSTU:"
Write-Host "     $($installer.FullName)"

# SHA256
$hash = Get-FileHash `
    -Path $installer.FullName `
    -Algorithm SHA256

"$($hash.Hash.ToLower())  $($installer.Name)" |
    Out-File `
        -FilePath "$($installer.FullName).sha256" `
        -Encoding ascii

Write-Host "[OK] SHA256 OLUSTU:"
Write-Host "     $($installer.FullName).sha256"

Write-Host ""
Write-Host "========================================"
Write-Host " WINDOWS EXE INSTALLER BASARILI"
Write-Host "========================================"
Write-Host ""
Write-Host "Installer:"
Write-Host "$($installer.FullName)"
Write-Host ""

