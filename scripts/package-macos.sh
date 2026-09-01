#!/bin/bash
set -e

echo "[INFO] Maven build basliyor..."

mvn clean package

if [ $? -ne 0 ]; then
    echo "[ERROR] Maven build basarisiz oldu."
    exit 1
fi

echo "[INFO] Release klasoru hazirlaniyor..."

rm -rf release/macos
mkdir -p release/macos

JAR=$(find target -maxdepth 1 -type f -name "*.jar" ! -name "*sources*" ! -name "*javadoc*" | head -n 1)

if [ -z "$JAR" ]; then
    echo "[ERROR] JAR dosyasi bulunamadi."
    exit 1
fi

echo "[INFO] JAR bulundu: $JAR"

echo "[INFO] jpackage calistiriliyor..."

jpackage \
    --type dmg \
    --name AeroGamecenter \
    --input target \
    --main-jar "$(basename "$JAR")" \
    --dest release/macos

if [ $? -ne 0 ]; then
    echo "[ERROR] jpackage basarisiz oldu."
    exit 1
fi

DMG=$(find release/macos -maxdepth 1 -type f -name "*.dmg" | head -n 1)

if [ -z "$DMG" ]; then
    echo "[ERROR] DMG dosyasi olusturulamadi."
    exit 1
fi

echo "[INFO] DMG olusturuldu: $DMG"

shasum -a 256 "$DMG" > "$DMG.sha256"

echo "[INFO] SHA256 olusturuldu."
echo "[INFO] macOS paketi basariyla tamamlandi."
