package se.scalablesolutions.akka.camel.component;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultConsumer;
import org.atmosphere.cpr.AtmosphereResourceImpl;
import org.mortbay.jetty.client.ContentExchange;
import org.mortbay.jetty.client.HttpClient;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <b>Experimental hack</b>.
 *
 * @author Martin Krasser
 */
public class AtmosphereConsumer extends DefaultConsumer implements Runnable {

    private HttpClient client;

    private ExecutorService executor;

    public AtmosphereConsumer(Endpoint endpoint, Processor processor) {
        super(endpoint, processor);
    }
    
    @Override
    public AtmosphereEndpoint getEndpoint() {
        return (AtmosphereEndpoint)super.getEndpoint();
    }

    public void run() {
        while((isStarting() || isStarted()) && !(client.isStopping() || client.isStopped())) {
            ContentExchange contentExchange = new ContentExchange();
            contentExchange.setURL(String.format("http://%s:%d/atmosphere",
                    getEndpoint().getHost(),
                    getEndpoint().getPort()));
            contentExchange.setMethod("GET");
            try {
                // long polling
                client.send(contentExchange);
                contentExchange.waitForDone();
                // message received
                onMessage(AtmosphereResourceHelper.removePrefixAndTrim(contentExchange.getResponseContent()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected void onMessage(String message) throws Exception {
        Exchange exchange = getEndpoint().createExchange();
        exchange.getIn().setBody(message);
        getProcessor().process(exchange);
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();
        client = new HttpClient();
        client.start();
        executor = Executors.newSingleThreadExecutor();
        executor.execute(this);
    }

    @Override
    protected void doStop() throws Exception {
        executor.shutdownNow();
        client.stop();
        super.doStop();
    }

    private static class AtmosphereResourceHelper extends AtmosphereResourceImpl {
        private static int prefixLength = createCompatibleStringJunk().length();

        private AtmosphereResourceHelper() {
            super(null, null, null, null, null);
        }

        public static String removePrefixAndTrim(String message) {
            return message.substring(prefixLength).trim();
        }

    }
}
