#!/bin/bash
set -e

echo "[INFO] Smoke test basliyor..."

if [ ! -d "release/linux" ]; then
    echo "[ERROR] release/linux klasoru bulunamadi."
    exit 1
fi

DEB=$(find release/linux -maxdepth 1 -type f -name "*.deb" | head -n 1)

if [ -z "$DEB" ]; then
    echo "[ERROR] DEB dosyasi bulunamadi."
    exit 1
fi

echo "[INFO] DEB bulundu: $DEB"

SHA=$(find release/linux -maxdepth 1 -type f -name "*.deb.sha256" | head -n 1)

if [ -z "$SHA" ]; then
    echo "[ERROR] SHA256 dosyasi bulunamadi."
    exit 1
fi

echo "[INFO] SHA256 bulundu: $SHA"
echo "[INFO] Smoke test basariyla tamamlandi."
