#!/bin/bash
set -euo pipefail

echo "========================================"
echo " AeroGamecenter macOS DMG Package"
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
    -print |
    head -n 1)

if [ -z "$JAR" ]; then
    echo "[ERROR] target klasorunde JAR bulunamadi."
    exit 1
fi

echo "[OK] JAR: $JAR"

echo ""
echo "[3/5] Release klasoru hazirlaniyor..."

rm -rf release/macos
mkdir -p release/macos

echo ""
echo "[4/5] macOS DMG installer olusturuluyor..."

jpackage \
    --type dmg \
    --name "AeroGamecenter" \
    --app-version "1.0.0" \
    --input "target" \
    --main-jar "$(basename "$JAR")" \
    --main-class "RobloxTrackerApp" \
    --dest "release/macos" \
    --icon "assets/AeroGamecenter.icns" 

echo "[OK] jpackage tamamlandi."

echo ""
echo "[5/5] DMG kontrol ediliyor..."

DMG=$(find release/macos -maxdepth 1 -type f -name "*.dmg" | head -n 1)

if [ -z "$DMG" ]; then
    echo "[ERROR] DMG dosyasi olusturulamadi."
    exit 1
fi

echo "[OK] DMG OLUSTU:"
echo "     $DMG"

shasum -a 256 "$DMG" > "$DMG.sha256"

echo "[OK] SHA256 OLUSTU:"
echo "     $DMG.sha256"

echo ""
echo "========================================"
echo " MACOS DMG PACKAGE BASARILI"
echo "========================================"
