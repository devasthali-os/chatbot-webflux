#!/usr/bin/env bash
set -euo pipefail

MODEL="${1:-llama3.2:1b}"

echo "Pulling Ollama model: ${MODEL}"
ollama pull "${MODEL}"
echo "Done. Run: ollama run ${MODEL}"
