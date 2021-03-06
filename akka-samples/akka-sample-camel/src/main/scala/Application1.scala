package sample.camel

import se.scalablesolutions.akka.actor.Actor
import se.scalablesolutions.akka.camel.Message
import se.scalablesolutions.akka.remote.RemoteClient

/**
 * @author Martin Krasser
 */
object Application1 {

  //
  // TODO: completion of example
  //

  def main(args: Array[String]) {
    implicit val sender: Option[Actor] = None

    val actor1 = new RemoteActor1
    val actor2 = RemoteClient.actorFor("remote2", "localhost", 7777)

    actor1.start

    println(actor1 !! Message("actor1"))
    println(actor2 !! Message("actor2"))
  }

}