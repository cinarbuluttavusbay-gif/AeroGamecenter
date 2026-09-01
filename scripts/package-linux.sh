#!/bin/bash
set -e

echo "[INFO] Maven build basliyor..."

mvn clean package

if [ $? -ne 0 ]; then
    echo "[ERROR] Maven build basarisiz oldu."
    exit 1
fi

echo "[INFO] Release klasoru hazirlaniyor..."

rm -rf release/linux
mkdir -p release/linux

JAR=$(find target -maxdepth 1 -type f -name "*.jar" ! -name "*sources*" ! -name "*javadoc*" | head -n 1)

if [ -z "$JAR" ]; then
    echo "[ERROR] JAR dosyasi bulunamadi."
    exit 1
fi

echo "[INFO] JAR bulundu: $JAR"

echo "[INFO] jpackage calistiriliyor..."

jpackage \
  --type deb \
  --name AeroGamecenter \
  --input target \
  --main-jar "$(basename "$JAR")" \
  --dest release/linux

if [ $? -ne 0 ]; then
    echo "[ERROR] jpackage basarisiz oldu."
    exit 1
fi

DEB=$(find release/linux -maxdepth 1 -type f -name "*.deb" | head -n 1)

if [ -z "$DEB" ]; then
    echo "[ERROR] DEB dosyasi olusturulamadi."
    exit 1
fi

echo "[INFO] DEB olusturuldu: $DEB"

sha256sum "$DEB" > "$DEB.sha256"

echo "[INFO] SHA256 olusturuldu."
echo "[INFO] Linux paketi basariyla tamamlandi."
