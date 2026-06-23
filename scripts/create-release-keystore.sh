#!/usr/bin/env bash
set -euo pipefail

KEYSTORE_FILE="${1:-release.keystore}"
KEY_ALIAS="${2:-qrscanner}"

if [ -f "$KEYSTORE_FILE" ]; then
  echo "Keystore already exists: $KEYSTORE_FILE"
  exit 1
fi

read -r -s -p "Keystore password: " STORE_PASS
echo
read -r -s -p "Key password (Enter for same): " KEY_PASS
echo
KEY_PASS="${KEY_PASS:-$STORE_PASS}"

keytool -genkeypair -v \
  -keystore "$KEYSTORE_FILE" \
  -alias "$KEY_ALIAS" \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000 \
  -storepass "$STORE_PASS" \
  -keypass "$KEY_PASS" \
  -dname "CN=QR Scanner, OU=Mobile, O=zapk13, C=US"

echo
echo "Keystore created: $KEYSTORE_FILE"
echo
echo "Add these GitHub repository secrets (Settings -> Secrets and variables -> Actions):"
echo "  ANDROID_KEYSTORE_BASE64 = $(base64 -w0 "$KEYSTORE_FILE" 2>/dev/null || base64 "$KEYSTORE_FILE" | tr -d '\n')"
echo "  ANDROID_KEYSTORE_PASSWORD = (your keystore password)"
echo "  ANDROID_KEY_ALIAS         = $KEY_ALIAS"
echo "  ANDROID_KEY_PASSWORD      = (your key password)"
echo
echo "Keep $KEYSTORE_FILE private. It is gitignored."
