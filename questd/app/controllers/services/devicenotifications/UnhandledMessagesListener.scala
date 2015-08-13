package controllers.services.devicenotifications

import akka.actor.{Actor, Props, UnhandledMessage}
import play.Logger
import play.libs.Akka

object UnhandledMessagesListener {
  val name = "UnhandledMessagesListener"
  val props = Props(classOf[UnhandledMessagesListener])
}


/**
 * Listener for unhandled messages.
 * TODO: move it to hierarchy of infrastructure actors.
 * Created by Yury on 11.08.2015.
 */
class UnhandledMessagesListener extends Actor {

  override def preStart() {
    Akka.system.eventStream.subscribe(self, classOf[UnhandledMessage])
  }

  override def postStop(): Unit = {
    Akka.system.eventStream.unsubscribe(self)
  }

  def receive = {
    case m: UnhandledMessage => Logger.error(s"$m")
  }
}
