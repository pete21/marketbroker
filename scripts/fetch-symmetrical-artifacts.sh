#!/usr/bin/env bash
set -euo pipefail

NEXUS_URL="https://github.com/SymmetricalAI/"
REPO_ROOT="$(cd "$(dirname "$0")/.." && pwd)/repository/ai/symmetrical"

ARTIFACTS=(
  "json-log/1.1.0/json-log-1.1.0.jar"
  "sym-cors/2.0.0/sym-cors-2.0.0.jar"
  "sym-kafka/7.4.1/sym-kafka-7.4.1.jar"
  "sym-kafka-annotations/1.0.0/sym-kafka-annotations-1.0.0.jar"
)

for artifact in "${ARTIFACTS[@]}"; do
  dest="${REPO_ROOT}/${artifact}"
  url="${NEXUS_URL}/ai/symmetrical/${artifact}"
  mkdir -p "$(dirname "$dest")"
  echo "Fetching ${artifact}..."
  curl -fsSL -o "${dest}" "${url}"
done

echo "Done. Artifacts saved under ${REPO_ROOT}"
