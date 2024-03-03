FROM container-registry.oracle.com/graalvm/jdk:21
RUN mkdir -p /paths/logs

COPY ./copy/main-0.1.jar /main-0.1.jar
ENTRYPOINT [ "java", "-Xms8G", "-Xmx320G", "-XX:MaxInlineLevel=32", "-XX:MaxInlineSize=512", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "-jar", "/main-0.1.jar" ]
