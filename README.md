Edit this is fixed in Jetty 9.4.22 via https://github.com/eclipse/jetty.project/issues/4183

Reproduces the following error:

```
Caused by:
java.lang.ClassNotFoundException: io.opentelemetry.javaagent.instrumentation.springwebmvc.HandlerMappingResourceNameFilter
	at org.eclipse.jetty.webapp.WebAppClassLoader.loadClass(WebAppClassLoader.java:565)
	at java.base/java.lang.ClassLoader.loadClass(Unknown Source)
	at java.base/java.lang.Class.forName0(Native Method)
	at java.base/java.lang.Class.forName(Unknown Source)
	at org.springframework.util.ClassUtils.forName(ClassUtils.java:275)
	at org.springframework.beans.factory.support.AbstractBeanDefinition.resolveBeanClass(AbstractBeanDefinition.java:437)
	at org.springframework.beans.factory.support.AbstractBeanFactory.doResolveBeanClass(AbstractBeanFactory.java:1457)
	at org.springframework.beans.factory.support.AbstractBeanFactory.resolveBeanClass(AbstractBeanFactory.java:1384)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.determineTargetType(AbstractAutowireCapableBeanFactory.java:680)
	at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.predictBeanType(AbstractAutowireCapableBeanFactory.java:647)
	at org.springframework.beans.factory.support.AbstractBeanFactory.isFactoryBean(AbstractBeanFactory.java:1518)
	at org.springframework.beans.factory.support.AbstractBeanFactory.isFactoryBean(AbstractBeanFactory.java:1023)
	at org.springframework.beans.factory.support.DefaultListableBeanFactory.preInstantiateSingletons(DefaultListableBeanFactory.java:829)
	at org.springframework.context.support.AbstractApplicationContext.finishBeanFactoryInitialization(AbstractApplicationContext.java:877)
	at org.springframework.context.support.AbstractApplicationContext.refresh(AbstractApplicationContext.java:549)
	at org.springframework.web.servlet.FrameworkServlet.configureAndRefreshWebApplicationContext(FrameworkServlet.java:701)
	at org.springframework.web.servlet.FrameworkServlet.createWebApplicationContext(FrameworkServlet.java:667)
	at org.springframework.web.servlet.FrameworkServlet.createWebApplicationContext(FrameworkServlet.java:715)
	at org.springframework.web.servlet.FrameworkServlet.initWebApplicationContext(FrameworkServlet.java:590)
	at org.springframework.web.servlet.FrameworkServlet.initServletBean(FrameworkServlet.java:529)
	at org.springframework.web.servlet.HttpServletBean.init(HttpServletBean.java:169)
	at javax.servlet.GenericServlet.init(GenericServlet.java:244)
	at org.eclipse.jetty.servlet.ServletHolder$WrapperServlet.init(ServletHolder.java:1287)
	at org.eclipse.jetty.servlet.ServletHolder.initServlet(ServletHolder.java:599)
	at org.eclipse.jetty.servlet.ServletHolder.initialize(ServletHolder.java:425)
	at org.eclipse.jetty.servlet.ServletHandler.lambda$initialize$0(ServletHandler.java:751)
	at java.base/java.util.stream.SortedOps$SizedRefSortingSink.end(Unknown Source)
	at java.base/java.util.stream.AbstractPipeline.copyInto(Unknown Source)
	at java.base/java.util.stream.AbstractPipeline.wrapAndCopyInto(Unknown Source)
	at java.base/java.util.stream.StreamSpliterators$WrappingSpliterator.forEachRemaining(Unknown Source)
	at java.base/java.util.stream.Streams$ConcatSpliterator.forEachRemaining(Unknown Source)
	at java.base/java.util.stream.Streams$ConcatSpliterator.forEachRemaining(Unknown Source)
	at java.base/java.util.stream.ReferencePipeline$Head.forEach(Unknown Source)
	at org.eclipse.jetty.servlet.ServletHandler.initialize(ServletHandler.java:744)
	at org.eclipse.jetty.servlet.ServletContextHandler.startContext(ServletContextHandler.java:361)
	at org.eclipse.jetty.webapp.WebAppContext.startWebapp(WebAppContext.java:1443)
	at org.eclipse.jetty.webapp.WebAppContext.startContext(WebAppContext.java:1407)
	at org.eclipse.jetty.server.handler.ContextHandler.doStart(ContextHandler.java:821)
	at org.eclipse.jetty.servlet.ServletContextHandler.doStart(ServletContextHandler.java:276)
	at org.eclipse.jetty.webapp.WebAppContext.doStart(WebAppContext.java:524)
	at org.eclipse.jetty.util.component.AbstractLifeCycle.start(AbstractLifeCycle.java:72)
	at org.eclipse.jetty.util.component.ContainerLifeCycle.start(ContainerLifeCycle.java:169)
	at org.eclipse.jetty.util.component.ContainerLifeCycle.doStart(ContainerLifeCycle.java:117)
	at org.eclipse.jetty.server.handler.AbstractHandler.doStart(AbstractHandler.java:106)
	at org.eclipse.jetty.util.component.AbstractLifeCycle.start(AbstractLifeCycle.java:72)
	at org.eclipse.jetty.util.component.ContainerLifeCycle.start(ContainerLifeCycle.java:169)
	at org.eclipse.jetty.server.Server.start(Server.java:407)
	at org.eclipse.jetty.util.component.ContainerLifeCycle.doStart(ContainerLifeCycle.java:110)
	at org.eclipse.jetty.server.handler.AbstractHandler.doStart(AbstractHandler.java:106)
	at org.eclipse.jetty.server.Server.doStart(Server.java:371)
	at org.eclipse.jetty.util.component.AbstractLifeCycle.start(AbstractLifeCycle.java:72)
	at example.ApplicationMain.main(ApplicationMain.java:44)
```

When attaching the OTEL Java Agent against an application where:

1. The main application hosts embedded Jetty + Spring MVC dependencies
2. The WAR is a simple layer that just hosts web service artifacts (controller), leveraging the common libraries (Spring) from the main application server

```
mvn clean install
docker build -t otel-issue .
docker run otel-issue
```