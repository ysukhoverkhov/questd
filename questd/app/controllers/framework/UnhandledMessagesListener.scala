package controllers.framework

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

  val system = Akka.system

  override def preStart() {
    system.eventStream.subscribe(self, classOf[UnhandledMessage])
  }

  override def postStop(): Unit = {
    system.eventStream.unsubscribe(self)
  }

  def receive = {
    case m: UnhandledMessage => Logger.error(s"$m")
  }
}
