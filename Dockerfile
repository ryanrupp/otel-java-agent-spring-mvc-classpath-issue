FROM openjdk:11.0.11-jre

WORKDIR /app

# Bring in dependencies/main app/WAR
COPY server/target/lib .
COPY server/target/app-server-0.0.1-SNAPSHOT.jar .
COPY web-service/target/*.war war/

# Pull down the java agent which we'll attach
# 1.7.0 = exception occurs (also occurs on current release 1.9.1)
# 1.6.2 = no exception
RUN wget --directory-prefix=/app/agent https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v1.7.0/opentelemetry-javaagent-all.jar
#RUN wget --directory-prefix=/app/agent https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v1.6.2/opentelemetry-javaagent-all.jar

CMD ["java", "-cp", "*", "-javaagent:/app/agent/opentelemetry-javaagent-all.jar", "-Dotel.traces.exporter=logging", "-Dotel.javaagent.debug=true", "example.ApplicationMain", "/app/war"]