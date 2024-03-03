#!/bin/bash

cd .. &&
cd .. &&
mvn clean install -DskipTests &&
cp ./main/target/*.jar ./docker/abricore/copy &&
cd ./docker/abricore &&
docker build -t io42630/abricore:latest . &&
echo "DONE."