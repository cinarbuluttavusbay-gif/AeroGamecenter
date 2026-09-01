#!/bin/bash
set -e

echo "[INFO] Smoke test basliyor..."

JAR=$(find target -maxdepth 1 -type f -name "AeroGamecenter-*.jar" ! -name "*sources*" ! -name "*javadoc*" | head -n 1)

if [ -z "$JAR" ]; then
    echo "[ERROR] JAR bulunamadi."
    exit 1
fi

echo "[INFO] JAR bulundu: $JAR"

if [ ! -d "release/linux" ]; then
    echo "[ERROR] release/linux klasoru bulunamadi."
    exit 1
fi

DEB=$(find release/linux -maxdepth 1 -type f -name "*.deb" | head -n 1)

if [ -z "$DEB" ]; then
    echo "[ERROR] DEB dosyasi bulunamadi."
    exit 1
fi

echo "[INFO] JAR kontrolu: OK"
echo "[INFO] DEB kontrolu: OK"
echo "[INFO] Smoke test basariyla tamamlandi."
