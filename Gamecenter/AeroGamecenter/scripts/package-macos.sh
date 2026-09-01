```bash
#!/bin/bash

set -e

mvn clean package

rm -rf release/macos
mkdir -p release/macos

JAR=$(find target -maxdepth 1 -name "*.jar" ! -name "*sources*" ! -name "*javadoc*" | head -n 1)

if [ -z "$JAR" ]; then
    echo "JAR dosyası bulunamadı."
    exit 1
fi

jpackage \
  --type dmg \
  --name AeroGamecenter \
  --input target \
  --main-jar "$(basename "$JAR")" \
  --dest release/macos

DMG=$(find release/macos -name "*.dmg" | head -n 1)

shasum -a 256 "$DMG" > "$DMG.sha256"
```

