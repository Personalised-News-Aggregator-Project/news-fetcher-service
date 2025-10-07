#!/bin/sh
# This script runs when the container starts

# Create the directory
mkdir -p /etc/secrets

# Decode the keystore from the environment variable into a file
echo $KEYSTORE_BASE64 | base64 -d > /etc/secrets/client.keystore.p12

# Decode the truststore from the environment variable into a file
echo $TRUSTSTORE_BASE64 | base64 -d > /etc/secrets/client.truststore.jks

# Run the main Java application
java -jar app.jar