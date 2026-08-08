#!/usr/bin/env bash
set -euo pipefail

echo "==> IA-TradeX: validando compilación..."
mvn -q clean compile

echo "==> IA-TradeX: compilación correcta."
echo "==> Para ejecutar: mvn javafx:run"
echo "==> Para empaquetar macOS: ./package-macos.sh"
