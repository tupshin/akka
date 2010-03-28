package se.scalablesolutions.akka.camel.component;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;
import org.atmosphere.cpr.AtmosphereServlet;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

import java.util.Map;

/**
 * <b>Experimental hack</b>.
 *
 * @author Martin Krasser
 */
public class AtmosphereComponent extends DefaultComponent {

    private int port = 8844;

    private Server server;

    public AtmosphereComponent() {
        this(null);
    }

    public AtmosphereComponent(CamelContext context) {
        super(context);
    }

    @Override
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> params) throws Exception {
        AtmosphereEndpoint atmosphereEndpoint = new AtmosphereEndpoint(uri);
        atmosphereEndpoint.setCamelContext(getCamelContext());
        return atmosphereEndpoint;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();

        //
        // Setup and start a single server for testing purposes
        //

        AtmosphereServlet atmosphereServlet = new AtmosphereServlet();
        atmosphereServlet.addAtmosphereHandler("/atmosphere", new DefaultHandler());

        Context context = new Context(Context.SESSIONS);
        context.setContextPath("/");
        context.addServlet(new ServletHolder(atmosphereServlet), "/atmosphere/*");

        server = new Server(port);
        server.setHandler(context);
        server.start();
    }

    @Override
    protected void doStop() throws Exception {
        server.stop();
        super.doStop();
    }
}
