SERVER_PORT=9999 \
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/rinha_db \
SPRING_DATASOURCE_USERNAME=postgres \
SPRING_DATASOURCE_PASSWORD=postgres \
SPRING_DATASOURCE_HIKARI_MAXIMUM_POOL_SIZE=10 \
SPRING_SQL_INIT_MODE=never \
SPRING_JPA_SHOW_SQL=false \
LOGGING_LEVEL_ROOT=error \
java -Xms1024m -Xmx1024m -Xss256k \
    -XX:-OmitStackTraceInFastThrow \
    -XX:StringTableSize=10000 \
    -XX:+UseStringDeduplication \
    -XX:MaxMetaspaceSize=128m \
    -XX:+UseG1GC \
    -jar target/rinhadev-0.0.1-SNAPSHOT.jar
