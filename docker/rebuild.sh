#!/bin/bash

#mvn clean install -DskipTests &&

cp ../main/target/*.jar ./abricore/copy/ &&
cp ../util/target/classes/*.properties ./abricore/copy/ &&

cd ./abricore &&



docker build -t io42630/abricore:latest . &