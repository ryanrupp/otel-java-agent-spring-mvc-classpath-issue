package example;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.logging.Logger;


/**
 * Reproduces an instrumentation issue if {@link Connection} is proxied.
 */
public class ReproduceReflectionProxyIssue {

    public static void setup() throws SQLException, ClassNotFoundException {
        // Register DB drivers
        Class.forName("org.h2.Driver");
        Class.forName(WrappingDriver.class.getName()); // would be a literal string usually but yeah force the load!

        // Grab a connection to force instrumentation exception
        System.out.println("Calling getConnection");
        try (Connection conn = DriverManager.getConnection("jdbc:wrapper:h2:mem:")) {
            // nothing to do
        }
        System.out.println("End getConnection");
    }

    private static class WrappingDriver implements Driver {

        private static final String WRAPPER_URL_PREFIX = "jdbc:wrapper:";

        static {
            try {
                DriverManager.registerDriver(new WrappingDriver());
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        @Override
        public Connection connect(String url, java.util.Properties info)
                throws SQLException {
            // Unwrap to the real JDBC URL and grab that
            String realUrl = "jdbc:" + url.substring(WRAPPER_URL_PREFIX.length());
            Connection real = DriverManager.getConnection(realUrl, info);

            // Proxy the connection, this causes an issue with instrumentation around DriverManager#connect
            return (Connection) Proxy.newProxyInstance(
                    ApplicationMain.class.getClassLoader(),
                    org.springframework.util.ClassUtils.getAllInterfaces(real),
                    new ConnectionProxy(real));
        }

        @Override
        public boolean acceptsURL(String url) {
            return url.startsWith(WRAPPER_URL_PREFIX);
        }

        @Override
        public DriverPropertyInfo[] getPropertyInfo(String url, java.util.Properties info) {
            return null;
        }

        @Override
        public int getMajorVersion() {
            return 1;
        }

        @Override
        public int getMinorVersion() {
            return 1;
        }

        @Override
        public boolean jdbcCompliant() {
            return true;
        }

        @Override
        public Logger getParentLogger() {
            return null;
        }
    }

    private static class ConnectionProxy implements InvocationHandler {

        private final Connection real;

        ConnectionProxy(Connection real) {
            this.real = real;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args)
                throws Throwable {
            System.out.println("Proxy invoke: " + method);
            return method.invoke(real, args);
        }
    }
}
