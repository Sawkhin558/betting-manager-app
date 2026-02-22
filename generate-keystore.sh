#!/bin/bash

# Keystore generation script for Betting Manager App
# Run this script to generate a keystore for signing release builds

set -e

KEYSTORE_FILE="app/keystore.jks"
KEY_ALIAS="bettingmanager"
KEY_PASSWORD="android"
KEYSTORE_PASSWORD="android"
VALIDITY_DAYS=10000

echo "Generating keystore for Betting Manager App..."
echo "=============================================="

if [ -f "$KEYSTORE_FILE" ]; then
    echo "Keystore already exists at: $KEYSTORE_FILE"
    echo "Do you want to overwrite it? (y/N)"
    read -r OVERWRITE
    if [[ ! "$OVERWRITE" =~ ^[Yy]$ ]]; then
        echo "Aborting."
        exit 0
    fi
    rm -f "$KEYSTORE_FILE"
fi

# Generate keystore
keytool -genkey -v \
    -keystore "$KEYSTORE_FILE" \
    -alias "$KEY_ALIAS" \
    -keyalg RSA \
    -keysize 2048 \
    -validity "$VALIDITY_DAYS" \
    -storepass "$KEYSTORE_PASSWORD" \
    -keypass "$KEY_PASSWORD" \
    -dname "CN=Betting Manager, OU=Development, O=Betting Inc, L=San Francisco, ST=California, C=US"

echo ""
echo "✅ Keystore generated successfully!"
echo ""
echo "Keystore location: $KEYSTORE_FILE"
echo "Alias: $KEY_ALIAS"
echo "Store password: $KEYSTORE_PASSWORD"
echo "Key password: $KEY_PASSWORD"
echo ""
echo "To encode the keystore for GitHub Secrets, run:"
echo "base64 -w 0 $KEYSTORE_FILE"
echo ""
echo "Save the base64 output as KEYSTORE_BASE64 GitHub Secret"
echo "Also set these GitHub Secrets:"
echo "KEYSTORE_PASSWORD: $KEYSTORE_PASSWORD"
echo "KEY_ALIAS: $KEY_ALIAS"
echo "KEY_PASSWORD: $KEY_PASSWORD"
echo ""
echo "For local development, these credentials are already configured in build.gradle.kts"