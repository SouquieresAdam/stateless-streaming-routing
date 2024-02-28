FROM eclipse-temurin:17
ARG XMS=512m
ARG XMX=1024m
ARG APP=papi-reconciliation
ENV XMS_VAL=${XMS}
ENV XMX_VAL=${XMX}
ENV APP_VAL=${APP}
WORKDIR /app
COPY tools/jmx-exporter/jmx_prometheus_javaagent-0.19.0.jar /app/jmx-agent.jar
COPY tools/jmx-exporter/jmx_config.yml /app/jmx-config.yml
COPY ${APP_VAL}/target/${APP_VAL}-0.0.1-SNAPSHOT.jar /app/streams-app.jar
ENTRYPOINT ["sh", "-c", "ls -la /app && java -javaagent:/app/jmx-agent.jar=1234:/app/jmx-config.yml -Xms${XMS_VAL} -Xmx${XMX_VAL} -jar streams-app.jar"]