#!/bin/bash
set -e

echo "[INFO] Smoke test basliyor..."

if [ ! -f "target/AeroGamecenter-1.0.1.jar" ]; then
    echo "[ERROR] JAR bulunamadi."
    exit 1
fi

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
