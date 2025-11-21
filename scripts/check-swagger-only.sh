#!/usr/bin/env bash
# Simple script to check if the OpenAPI JSON is available
# Usage: ./check-swagger-only.sh [HOST] [PORT] [PATH]
HOST=${1:-localhost}
PORT=${2:-8080}
PATH_TO_API=${3:-/v3/api-docs}
URL="http://${HOST}:${PORT}${PATH_TO_API}"

HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" "$URL" || echo "000")
if [ "$HTTP_CODE" = "200" ]; then
  echo "OK: Swagger available at $URL"
  exit 0
else
  echo "FAIL: Swagger not available at $URL (HTTP $HTTP_CODE)"
  exit 1
fi
