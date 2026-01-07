#!/usr/bin/env bash
# Script para crear una base de datos Odoo si no existe
# Uso: ./create_odoo_db.sh <url> <master_pwd> <db_name> <admin_email> <admin_pass> <demo> <lang>
URL=${1:-http://localhost:8069}
MASTER=${2:-admin}
DB=${3:-garantias}
ADMIN_EMAIL=${4:-admin@example.com}
ADMIN_PASS=${5:-admin}
DEMO=${6:-true}
LANG=${7:-es_ES}

echo "Comprobando existencia de la BD '$DB' en $URL..."
LIST=$(curl -s -X POST -H 'Content-Type: application/json' -d '{"jsonrpc":"2.0","method":"call","params":{}}' "$URL/web/database/list")
if echo "$LIST" | grep -q "\"$DB\""; then
  echo "La base de datos '$DB' ya existe."
  exit 0
fi

echo "Creando base de datos '$DB'..."
CREATE_PAYLOAD=$(cat <<EOF
{"jsonrpc": "2.0", "method": "call", "params": {"master_pwd": "$MASTER", "name": "$DB", "demo": $DEMO, "lang": "$LANG", "admin_password": "$ADMIN_PASS", "admin_email": "$ADMIN_EMAIL"}}
EOF
)

RESP=$(curl -s -X POST -H 'Content-Type: application/json' -d "$CREATE_PAYLOAD" "$URL/web/database/create")
if echo "$RESP" | grep -q "result"; then
  echo "Solicitud enviada. Revisa los logs de Odoo si hace falta."
else
  echo "Error al crear la base de datos: $RESP"
  exit 1
fi
