/**
 * Copyright (C) 2009 Scalable Solutions.
 */

package sample.concurrency

import se.scalablesolutions.akka.actor.{SupervisorFactory, Actor}
import se.scalablesolutions.akka.config.ScalaConfig._
import se.scalablesolutions.akka.util.Logging

import java.lang.Integer
import javax.ws.rs.core.MultivaluedMap
import javax.ws.rs.{GET, POST, Path, Produces, WebApplicationException, Consumes}

class Boot {
  val factory = SupervisorFactory(
    SupervisorConfig(
	  RestartStrategy(OneForOne, 3, 100),
	  Supervise(
        new ConcurrentBrewingService,
        LifeCycle(Permanent))
      :: Nil))

  factory.newInstance.start
}

/**
 * Try service out by invoking:
 * <pre>
 * curl http://localhost:9998/concurrency/brewing
 * </pre>
 * Or browse to the URL from a web browser.
 */
@Path("/concurrency/brewing")
class ConcurrentBrewingService extends Actor {

  case object Brew

  @GET
  @Produces(Array("text/html"))
  def brew = (this !! Brew).getOrElse(<error>Error in brewing process</error>)

  def receive = {
    case Brew =>
      // Initialise 3 BrewActors initially for 3 different beer types.
      // The results should be an ingredients list for each of the different
      // beer types.
      /*val bitter = new BrewActor(params) 
      val germanLager = new BrewActor(params)
      val belgianLager = new BrewActor(params)*/
      reply(<success>Brewed</success>)
  }
}

/*class BrewActor extends Actor {
  //
}*/
