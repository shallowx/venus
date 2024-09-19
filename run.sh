#!/bin/bash

JAVA_OPTS="-XX:MinHeapSize=4g -XX:InitialHeapSize=4g -XX:MaxHeapSize=4g -XX:-UseLargePages -XX:+UseZGC -Dfile.encoding=UTF-8 -Duser.timezone=Asia/Shanghai -XX:+HeapDumpOnOutOfMemoryError -XX:+ExitOnOutOfMemoryError -XX:HeapDumpPath=/tmp/heapdump.hprof --add-exports java.base/jdk.internal.misc=ALL-UNNAMED --add-opens java.base/java.nio=ALL-UNNAMED --enable-preview"
exec /usr/bin/java  -Dspring.application.name=${appName} -jar $JAVA_OPTS -Dmanagement.server.port=${managementPort} -Dlogging.file.path=/var/logs/${appName} /app/venus-1.0.0-SNAPSHOT.jar