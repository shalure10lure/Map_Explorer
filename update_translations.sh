#!/bin/bash

# ==============================================================================
# Script para actualizar las traducciones desde Loco (Localise.biz)
# ==============================================================================

# Coloca aquí tu "Export Key" de Loco
LOCO_API_KEY="Eyh17pS2sPQABW-NAx3QiXwqwXGRn39y"

# Ruta relativa hacia los recursos de Compose Multiplatform
RES_DIR="composeApp/src/commonMain/composeResources"

echo "----------------------------------------------------------"
echo "Actualizando recursos de idiomas desde Loco..."
echo "----------------------------------------------------------"

# --- IDIOMA MADRE (Español - es) ---
# Se guarda en la carpeta 'values' por defecto
echo "[1/2] Descargando idioma base (es) -> values/strings.xml"
mkdir -p "$RES_DIR/values"
curl -X GET "https://localise.biz/api/export/locale/es.xml?key=${LOCO_API_KEY}&format=android" \
     -o "$RES_DIR/values/strings.xml"

# --- TRADUCCIÓN (Inglés - en) ---
# Se guarda en la carpeta 'values-en'
echo "[2/2] Descargando traducción (en) -> values-en/strings.xml"
mkdir -p "$RES_DIR/values-en"
curl -X GET "https://localise.biz/api/export/locale/en.xml?key=${LOCO_API_KEY}&format=android" \
     -o "$RES_DIR/values-en/strings.xml"

echo "----------------------------------------------------------"
echo "¡Proceso finalizado! Revisa tus archivos en:"
echo "$RES_DIR"
echo "----------------------------------------------------------"
