#!/bin/bash
mkdir -p build
javac src/main/java/infotecs/client/*.java src/main/java/infotecs/client/*/*.java -d build
jar cfm FTPClient.jar MANIFEST.MF -C build .
