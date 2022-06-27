#!/bin/zsh
./gradlew remapJar || exit
cp ./build/libs/gaslight-1.0-SNAPSHOT.jar /home/ada/.local/share/multimc/instances/1.19.1-rc11/.minecraft/mods/gaslight-1.0-SNAPSHOT.jar
multimc -l 1.19.1-rc11