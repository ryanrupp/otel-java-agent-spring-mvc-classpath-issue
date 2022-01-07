Reproduces the issue described in https://github.com/open-telemetry/opentelemetry-java-instrumentation/issues/5032

Output with Java 11:

```
[main] DEBUG io.opentelemetry.javaagent.bootstrap.ExceptionLogger - Failed to handle exception in instrumentation for example.ReproduceReflectionProxyIssue$WrappingDriver on jdk.internal.loader.ClassLoaders$AppClassLoader@2c13da15
java.lang.AbstractMethodError: Receiver class com.sun.proxy.$Proxy44 does not define or inherit an implementation of the resolved method abstract __set__opentelemetryVirtualField$java$sql$Connection$io$opentelemetry$javaagent$shaded$instrumentation$jdbc$internal$DbInfo(Ljava/lang/Object;)V of interface io.opentelemetry.javaagent.bootstrap.field.VirtualFieldAccessor$java$sql$Connection$io$opentelemetry$javaagent$shaded$instrumentation$jdbc$internal$DbInfo.
    at io.opentelemetry.javaagent.bootstrap.field.VirtualFieldImpl$java$sql$Connection$io$opentelemetry$javaagent$shaded$instrumentation$jdbc$internal$DbInfo.realPut(VirtualFieldImplementationsGenerator.java)
    at io.opentelemetry.javaagent.bootstrap.field.VirtualFieldImpl$java$sql$Connection$io$opentelemetry$javaagent$shaded$instrumentation$jdbc$internal$DbInfo.set(VirtualFieldImplementationsGenerator.java:282)
    at example.ReproduceReflectionProxyIssue$WrappingDriver.connect(ReproduceReflectionProxyIssue.java:49)
    at java.sql/java.sql.DriverManager.getConnection(DriverManager.java:677)
    at java.sql/java.sql.DriverManager.getConnection(DriverManager.java:251)
    at example.ReproduceReflectionProxyIssue.setup(ReproduceReflectionProxyIssue.java:23)
    at example.ApplicationMain.main(ApplicationMain.java:47)
```

Output with Java 8:

```
[main] DEBUG io.opentelemetry.javaagent.bootstrap.ExceptionLogger - Failed to handle exception in instrumentation for example.ReproduceReflectionProxyIssue$WrappingDriver on sun.misc.Launcher$AppClassLoader@18b4aac2
java.lang.AbstractMethodError
at io.opentelemetry.javaagent.bootstrap.field.VirtualFieldImpl$java$sql$Connection$io$opentelemetry$javaagent$shaded$instrumentation$jdbc$internal$DbInfo.realPut(VirtualFieldImplementationsGenerator.java)
at io.opentelemetry.javaagent.bootstrap.field.VirtualFieldImpl$java$sql$Connection$io$opentelemetry$javaagent$shaded$instrumentation$jdbc$internal$DbInfo.set(VirtualFieldImplementationsGenerator.java:282)
at example.ReproduceReflectionProxyIssue$WrappingDriver.connect(ReproduceReflectionProxyIssue.java:49)
at java.sql.DriverManager.getConnection(DriverManager.java:664)
at java.sql.DriverManager.getConnection(DriverManager.java:270)
at example.ReproduceReflectionProxyIssue.setup(ReproduceReflectionProxyIssue.java:23)
at example.ApplicationMain.main(ApplicationMain.java:47)
```

To build + reproduce:

```
mvn clean install
docker build -t otel-issue .
docker run otel-issue
```