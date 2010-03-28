package se.scalablesolutions.akka.camel.component;

import org.apache.camel.MultipleConsumersSupport;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultEndpoint;

import java.net.URI;

/**
 * <b>Experimental hack</b>.
 *
 * @author Martin Krasser
 */
public class AtmosphereEndpoint extends DefaultEndpoint implements MultipleConsumersSupport {

    private String host;

    private int port;

    public AtmosphereEndpoint(String uri) throws Exception {
        super(uri);
        URI uriobj = new URI(uri);
        this.host = uriobj.getHost();
        this.port = uriobj.getPort();
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public AtmosphereProducer createProducer() {
        return new AtmosphereProducer(this);
    }

    public AtmosphereConsumer createConsumer(Processor processor) {
        return new AtmosphereConsumer(this, processor);
    }

    public boolean isMultipleConsumersSupported() {
        return true;
    }

    public boolean isSingleton() {
        return false;
    }
}
