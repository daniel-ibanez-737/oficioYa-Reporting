#!/usr/bin/env bash
# Levanta Reporting en Docker y ejecuta su colección de Postman con Newman.
# Requisitos: Docker y haber compilado el proyecto (./mvnw verify o ./mvnw package).
# Uso, desde la raíz del proyecto (Git Bash, Linux o macOS):  bash postman/ejecutar-coleccion.sh
RAIZ="$(cd "$(dirname "$0")/.." && (pwd -W 2>/dev/null || pwd))"
PROYECTO=oficioya-reporting
SERVICIO=oy-reporting
RED=oy-reporting-red
export MSYS_NO_PATHCONV=1

limpiar() {
  docker rm -f "$SERVICIO" oy-reporting-pg >/dev/null 2>&1
  docker network rm "$RED" >/dev/null 2>&1
}
limpiar
docker network create "$RED" >/dev/null

docker run -d --rm --name "$SERVICIO" --network "$RED" -v "$RAIZ/target":/t eclipse-temurin:21-jre \
  sh -c "java -jar /t/$PROYECTO-*.jar --spring.profiles.active=demo" >/dev/null

for i in $(seq 1 60); do
  docker logs "$SERVICIO" 2>&1 | grep -q "Started Oficioya" && break
  sleep 2
done
echo "$SERVICIO: $(docker logs "$SERVICIO" 2>&1 | grep -o 'Started Oficioya[A-Za-z]* in [0-9.]* seconds' || echo 'NO ARRANCO')"

docker run --rm --network "$RED" -v "$RAIZ/postman":/etc/newman postman/newman:6.1.3-alpine run \
  OficioYa-Reporting.postman_collection.json \
  --env-var reportingUrl=http://$SERVICIO:8083 \
  --reporters cli --color off
RESULTADO=$?

echo "--- logs relevantes ---"
docker logs "$SERVICIO" 2>&1 | grep -E "Reporte (generado|exportado)" | cut -c1-220 | head -6
limpiar
echo "newman exit=$RESULTADO"
exit $RESULTADO
