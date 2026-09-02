#!/bin/bash
set -euo pipefail

echo "========================================"
echo " AeroGamecenter Linux DEB Package"
echo "========================================"

echo "[0/5] Java kontrol ediliyor..."

java -version
echo ""

echo "[INFO] jpackage:"
jpackage --version

echo ""
echo "[1/5] Maven build basliyor..."

mvn -B clean package

echo "[OK] Maven build tamamlandi."

echo ""
echo "[2/5] JAR dosyasi araniyor..."

JAR=$(find target -maxdepth 1 -type f -name "*.jar" \
    ! -name "*sources*" \
    ! -name "*javadoc*" \
    -printf '%s %p\n' |
    sort -nr |
    head -n 1 |
    cut -d' ' -f2-)

if [ -z "$JAR" ]; then
    echo "[ERROR] target klasorunde JAR bulunamadi."
    exit 1
fi

echo "[OK] JAR: $JAR"

echo ""
echo "[3/5] Release klasoru hazirlaniyor..."

rm -rf release/linux
mkdir -p release/linux

echo ""
echo "[4/5] Linux DEB installer olusturuluyor..."

jpackage \
    --type deb \
    --name "AeroGamecenter" \
    --app-version "1.0.0" \
    --input "target" \
    --main-jar "$(basename "$JAR")" \
    --main-class "RobloxTrackerApp" \
    --dest "release/linux" \
    --linux-shortcut \
    --linux-menu-group "AeroGamecenter"

echo "[OK] jpackage tamamlandi."

echo ""
echo "[5/5] DEB kontrol ediliyor..."

DEB=$(find release/linux -maxdepth 1 -type f -name "*.deb" | head -n 1)

if [ -z "$DEB" ]; then
    echo "[ERROR] DEB dosyasi olusturulamadi."
    exit 1
fi

echo "[OK] DEB OLUSTU:"
echo "     $DEB"

sha256sum "$DEB" > "$DEB.sha256"

echo "[OK] SHA256 OLUSTU:"
echo "     $DEB.sha256"

echo ""
echo "========================================"
echo " LINUX DEB PACKAGE BASARILI"
echo "========================================"
