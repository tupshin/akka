package sample.camel

import org.apache.camel.{Exchange, Processor}
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.spring.spi.ApplicationContextRegistry
import org.springframework.context.support.ClassPathXmlApplicationContext

import se.scalablesolutions.akka.actor.SupervisorFactory
import se.scalablesolutions.akka.camel.CamelContextManager
import se.scalablesolutions.akka.config.ScalaConfig._

/**
 * @author Martin Krasser
 */
class Boot {

  // Create CamelContext with Spring-based registry and custom route builder

  val context = new ClassPathXmlApplicationContext("/sample-camel-context.xml", getClass)
  val registry = new ApplicationContextRegistry(context)
  CamelContextManager.init(new DefaultCamelContext(registry))
  CamelContextManager.context.addRoutes(new CustomRouteBuilder)

  // Basic example

  val factory = SupervisorFactory(
    SupervisorConfig(
      RestartStrategy(OneForOne, 3, 100, List(classOf[Exception])),
      Supervise(new Consumer1, LifeCycle(Permanent)) ::
      Supervise(new Consumer2, LifeCycle(Permanent)) :: Nil))
  factory.newInstance.start

  // Routing example

  val producer = new Producer1
  val mediator = new Transformer(producer)
  val consumer = new Consumer3(mediator)

  producer.start
  mediator.start
  consumer.start

  // Publish subscribe example

  //
  // Cometd example is disabled because of unresolved sbt/ivy dependency resolution issues.
  // If you want to run this example, make sure to replace all jetty-*-6.1.22.jar files
  //  on the classpath with corresponding jetty-*-6.1.11.jar files.
  //

  //val cometdUri = "cometd://localhost:8111/test/abc?resourceBase=target"
  //val cometdSubscriber = new Subscriber("cometd-subscriber", cometdUri).start
  //val cometdPublisher = new Publisher("cometd-publisher", cometdUri).start

  val jmsUri = "jms:topic:test"
  val jmsSubscriber1 = new Subscriber("jms-subscriber-1", jmsUri).start
  val jmsSubscriber2 = new Subscriber("jms-subscriber-2", jmsUri).start
  val jmsPublisher = new Publisher("jms-publisher", jmsUri).start

  val atmoUri = "atmosphere://localhost:8844"
  val atmoSubscriber1 = new Subscriber("atmo-subscriber-1", atmoUri).start
  val atmoSubscriber2 = new Subscriber("atmo-subscriber-2", atmoUri).start
  val atmoSubscriber3 = new Subscriber("atmo-subscriber-3", atmoUri).start
  val atmoPublisher = new Publisher("atmo-publisher", atmoUri).start

  //val cometdPublisherBridge = new PublisherBridge("jetty:http://0.0.0.0:8877/camel/pub/cometd", cometdPublisher).start
  val jmsPublisherBridge = new PublisherBridge("jetty:http://0.0.0.0:8877/camel/pub/jms", jmsPublisher).start
  val atmoPublisherBridge = new PublisherBridge("jetty:http://0.0.0.0:8877/camel/pub/atmo", atmoPublisher).start

}

class CustomRouteBuilder extends RouteBuilder {
  def configure {
    val actorUri = "actor:%s" format classOf[Consumer2].getName
    from("jetty:http://0.0.0.0:8877/camel/test2").to(actorUri)
    from("direct:welcome").process(new Processor() {
      def process(exchange: Exchange) {
        exchange.getOut.setBody("Welcome %s" format exchange.getIn.getBody)
      }
    })
  }
}