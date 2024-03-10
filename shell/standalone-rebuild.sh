#!/bin/bash

cd .. &&
mvn clean install -DskipTests &&
java -Xms8G -Xmx100G -XX:MaxInlineLevel=256 -XX:MaxInlineSize=1024k -Xss256k -XX:MaxMetaspaceSize=512m -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:24003 -jar ./main/target/main-0.1.jar