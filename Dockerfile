FROM openjdk:11.0.11-jre

WORKDIR /app

# Bring in dependencies/main app/WAR
COPY server/target/lib .
COPY server/target/app-server-0.0.1-SNAPSHOT.jar .
COPY web-service/target/*.war war/

# Pull down the java agent which we'll attach
RUN wget --directory-prefix=/app/agent https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v1.2.0/opentelemetry-javaagent-all.jar

# can toggle spring-webmvc off here to get a working launch
CMD ["java", "-cp", "*", "-javaagent:/app/agent/opentelemetry-javaagent-all.jar", "-Dotel.traces.exporter=logging", "-Dotel.instrumentation.spring-webmvc.enabled=true", "example.ApplicationMain", "/app/war"]