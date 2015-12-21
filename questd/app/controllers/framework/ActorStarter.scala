package controllers.framework

import play.libs.Akka

/**
 * Starts all actors for now.
 */
class ActorStarter {

  val listener = Akka.system.actorOf(UnhandledMessagesListener.props, name = UnhandledMessagesListener.name)
}
