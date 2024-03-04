#!/bin/bash
cd .. &&
java -Xms8G -Xmx140G -XX:MaxInlineLevel=32 -XX:MaxInlineSize=512 -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:24003 -jar ./target/main-0.1.jar