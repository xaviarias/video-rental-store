package net.xas.vrs.server;

import net.xas.vrs.api.VideoRentalApp;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

/**
 * Runs the Video rental store as an executable JAR.
 */
public class Main {

    public static void main(String[] args) throws Exception {
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        Server jettyServer = new Server(8080);
        jettyServer.setHandler(context);

        VideoRentalApp videoRentalApp = new VideoRentalApp();
        ServletContainer container = new ServletContainer(videoRentalApp);

        ServletHolder jerseyServlet = new ServletHolder(container);
        jerseyServlet.setInitOrder(1);

        context.addServlet(jerseyServlet, "/*");

        try {
            jettyServer.start();
            jettyServer.join();
        } finally {
            jettyServer.destroy();
        }
    }

}
