#!/bin/bash

mvn clean install -DskipTests &&
cp ./main/target/*.jar ./docker/abricore/copy &&
cd ./docker/abricore &&
docker build -t io42630/abricore:latest . &&
echo "DONE." &&
docker compose up abricore-db -d &&
docker compose up abricore --force-recreate -d &&
echo "DONE."
