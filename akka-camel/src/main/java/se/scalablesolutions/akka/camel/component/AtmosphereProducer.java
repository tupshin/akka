package se.scalablesolutions.akka.camel.component;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.mortbay.io.ByteArrayBuffer;
import org.mortbay.jetty.client.ContentExchange;
import org.mortbay.jetty.client.HttpClient;

/**
 * <b>Experimental hack</b>.
 *
 * @author Martin Krasser
 */
public class AtmosphereProducer extends DefaultProducer {

    private HttpClient client;

    public AtmosphereProducer(AtmosphereEndpoint endpoint) {
        super(endpoint);
    }

    public void process(Exchange exchange) throws Exception {
        ContentExchange contentExchange = new ContentExchange();
        contentExchange.setURL(String.format("http://%s:%d/atmosphere",
                getEndpoint().getHost(),
                getEndpoint().getPort()));
        contentExchange.setMethod("POST");
        contentExchange.setRequestContent(new ByteArrayBuffer(exchange.getIn().getBody(byte[].class)));
        client.send(contentExchange);
    }

    @Override
    public AtmosphereEndpoint getEndpoint() {
        return (AtmosphereEndpoint)super.getEndpoint();
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();
        client = new HttpClient();
        client.start();
    }

    @Override
    protected void doStop() throws Exception {
        client.stop();
        super.doStop();
    }
}
