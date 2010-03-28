package se.scalablesolutions.akka.camel.component;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Martin Krasser
 */
public class AtmosphereComponentTest {

    CamelContext context;
    ProducerTemplate template;

    MockEndpoint mock1;
    MockEndpoint mock2;

    @Before
    public void setUp() throws Exception {
        context = new DefaultCamelContext();
        context.addRoutes(new TestROuteBuilder());
        template = context.createProducerTemplate();

        context.start();
        template.start();

        mock1 = (MockEndpoint)context.getEndpoint("mock:mock1");
        mock2 = (MockEndpoint)context.getEndpoint("mock:mock2");
    }

    @After
    public void tearDown() throws Exception {
        template.stop();
        context.stop();
    }

    @Test
    public void testRoute() throws Exception {
        mock1.expectedBodiesReceived("test");
        mock2.expectedBodiesReceived("test");
        // Wait for consumers to start
        // TODO: use countdown latches 
        Thread.sleep(1000);
        template.sendBody("direct:test", "test");
        mock1.assertIsSatisfied();
        mock2.assertIsSatisfied();
    }

    private static class TestROuteBuilder extends RouteBuilder {

        @Override
        public void configure() throws Exception {
            from("direct:test").to("atmosphere://localhost:8844");
            from("atmosphere://localhost:8844").to("mock:mock1");
            from("atmosphere://localhost:8844").to("mock:mock2");
        }
    }

}
