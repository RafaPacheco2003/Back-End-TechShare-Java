#!/bin/sh
# wait-for-db.sh: espera hasta que el puerto TCP de MySQL esté abierto y luego arranca la app

HOST=${SPRING_DATASOURCE_HOST:-$(echo "$SPRING_DATASOURCE_URL" | sed -n 's|.*//\([^:/]*\).*|\1|p')}
PORT=${SPRING_DATASOURCE_PORT:-3306}

if [ -z "$HOST" ] || [ "$HOST" = "" ]; then
  HOST=db
fi

echo "Waiting for TCP $HOST:$PORT ..."
RETRIES=60
COUNT=0
while [ $COUNT -lt $RETRIES ]; do
  if command -v nc >/dev/null 2>&1; then
    if nc -z -w2 "$HOST" "$PORT" >/dev/null 2>&1; then
      echo "TCP $HOST:$PORT open"
      break
    fi
  else
    # fallback: intentar conectar con /dev/tcp (busybox/sh may soportarlo)
    (echo > /dev/tcp/$HOST/$PORT) >/dev/null 2>&1 && { echo "TCP $HOST:$PORT open"; break; } || true
  fi
  COUNT=$((COUNT+1))
  echo "Waiting for TCP $HOST:$PORT... ($COUNT/$RETRIES)"
  sleep 2
done

if [ $COUNT -ge $RETRIES ]; then
  echo "Timed out waiting for MySQL after $RETRIES attempts" >&2
  exec java -jar app.jar
  exit 1
fi

echo "Starting application..."
exec java -jar app.jar
