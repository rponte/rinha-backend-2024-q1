#!/bin/bash
set -x # echo on

SERVER_PORT=9999 \
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/rinha_db \
SPRING_DATASOURCE_USERNAME=postgres \
SPRING_DATASOURCE_PASSWORD=postgres \
SPRING_DATASOURCE_HIKARI_MAXIMUM_POOL_SIZE=20 \
SPRING_DATASOURCE_HIKARI_CONNECTIONTIMEOUT=5000 \
SPRING_SQL_INIT_MODE=never \
SPRING_JPA_SHOW_SQL=false \
LOGGING_LEVEL_ROOT=error \
java -Xms512m -Xmx512m \
    -XX:MaxMetaspaceSize=256m \
    -Xlog:gc*:file=app-gc.log \
    -Dcom.sun.management.jmxremote \
    -jar target/rinhadev-0.0.1-SNAPSHOT.jar


#java -Xms512m -Xms512m \
#    -XX:MaxMetaspaceSize=256m \
#    -Xss256k \
#    -XX:-OmitStackTraceInFastThrow \
#    -XX:StringTableSize=10000 \
#    -XX:+UseStringDeduplication \
#    -XX:+UseG1GC \
#    -jar target/rinhadev-0.0.1-SNAPSHOT.jar