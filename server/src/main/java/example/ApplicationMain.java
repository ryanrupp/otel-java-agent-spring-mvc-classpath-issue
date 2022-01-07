package example;

import org.eclipse.jetty.deploy.DeploymentManager;
import org.eclipse.jetty.deploy.providers.WebAppProvider;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;


/**
 * Launches embedded Jetty that scans for WARs
 */
public class ApplicationMain {

    public static void main(String args[]) throws Exception {
        String warsLocation = args.length > 0 ? args[0] : "./web-service/target";

        ContextHandlerCollection contexts = new ContextHandlerCollection();
        ContextHandler apiContext = new ContextHandler();
        apiContext.setContextPath("/api");
        contexts.addHandler(apiContext);

        DeploymentManager manager = new DeploymentManager();
        manager.setContexts(contexts);
        WebAppProvider provider = new WebAppProvider();
        // Webapp classloader should take precedence
        // Thought this maybe mattered but toggling it true results in the same behavior
        provider.setParentLoaderPriority(false);
        provider.setExtractWars(true);
        provider.setScanInterval(10);
        provider.setMonitoredDirName(warsLocation);
        manager.addAppProvider(provider);

        Server server = new Server();
        server.addBean(manager);

        ServerConnector connector = new ServerConnector(server);
        connector.setPort(8090);
        server.setConnectors(new Connector[]{connector});
        server.setHandler(contexts);

        server.start();

        // Make the exception happen!
        ReproduceReflectionProxyIssue.setup();
    }
}