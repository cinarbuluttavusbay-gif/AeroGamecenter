```bash
#!/bin/bash

set -e

mvn clean package

rm -rf release/linux
mkdir -p release/linux

JAR=$(find target -maxdepth 1 -name "*.jar" ! -name "*sources*" ! -name "*javadoc*" | head -n 1)

if [ -z "$JAR" ]; then
    echo "JAR dosyası bulunamadı."
    exit 1
fi

jpackage \
  --type deb \
  --name Gamecenter \
  --input target \
  --main-jar "$(basename "$JAR")" \
  --dest release/linux

DEB=$(find release/linux -name "*.deb" | head -n 1)

sha256sum "$DEB" > "$DEB.sha256"
```

