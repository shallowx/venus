#!/bin/bash

base_dir=$(dirname $0)
set -e

if [[ -z ${JAVA_OPTS} ]]; then
  JAVA_OPTS="-XX:MinHeapSize=4g -XX:InitialHeapSize=12g -XX:MaxHeapSize=12g -XX:-UseLargePages -XX:+UseZGC -Dfile.encoding=UTF-8 -Duser.timezone=Asia/Shanghai -XX:+HeapDumpOnOutOfMemoryError -XX:+ExitOnOutOfMemoryError -XX:HeapDumpPath=/tmp/heapdump.hprof -Dlogging.file.path=/var/logs/${app}/ --add-exports java.base/jdk.internal.misc=ALL-UNNAMED --add-opens java.base/java.nio=ALL-UNNAMED --enable-preview"
fi

if [[ -n ${JAVA_HOME} ]]; then
    if [[ -z ${JAVA_EXE} ]]; then
        JAVA_EXE=${JAVA_HOME}/bin/java
    fi
fi

if [[ -z ${JAVA_EXE} ]]; then
    JAVA_EXE=java
fi

for file in "$base_dir"/vault-proxy.jar
do
  METEOR_JAR_FILE=${file}
done

$JAVA_EXE -server ${JAVA_OPTS} -jar ${METEOR_JAR_FILE} "$@"