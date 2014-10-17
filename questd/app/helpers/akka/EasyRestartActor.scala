package helpers.akka

import akka.actor.Actor

trait EasyRestartActor extends Actor {

  override def preStart(): Unit = ()

  override def postRestart(reason: Throwable): Unit = ()

  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    postStop()
  }

}

