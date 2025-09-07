#!/usr/bin/env sh
# Gradle start script for Unix

DIRNAME=$(dirname "$0")
cd "$DIRNAME"

exec java -jar gradle/wrapper/gradle-wrapper.jar "$@"
