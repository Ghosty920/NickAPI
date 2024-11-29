#!/bin/bash

if [ ! -d "BuildTools" ]; then
    mkdir "BuildTools"
fi

cd "BuildTools" || { echo "[FAIL] Can't access ./BuildTools/"; exit 1; };
curl -O "https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar"

if [ -f "BuildTools.jar" ]; then
    echo "[INFO] BuildTools.jar downloaded."
else
    echo "[FAIL] BuildTools.jar couldn't be found."
    exit 1
fi

java -jar BuildTools.jar --nogui --rev 1.20.1 --remapped --compile SPIGOT
java -jar BuildTools.jar --nogui --rev 1.20.2 --remapped --compile SPIGOT
java -jar BuildTools.jar --nogui --rev 1.20.4 --remapped --compile SPIGOT
java -jar BuildTools.jar --nogui --rev 1.20.6 --remapped --compile SPIGOT
java -jar BuildTools.jar --nogui --rev 1.21.1 --remapped --compile SPIGOT
java -jar BuildTools.jar --nogui --rev 1.21.3 --remapped --compile SPIGOT