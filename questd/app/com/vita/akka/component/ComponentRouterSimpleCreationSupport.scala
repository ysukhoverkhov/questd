package com.vita.akka.component

import akka.actor.{ActorRef, ActorSystem}
import com.vita.akka.component.impl.SimpleRouter

/**
 * Support for creating routers for messages.
 *
 * Created by Yury on 01.10.2015.
 */
trait ComponentRouterSimpleCreationSupport extends ComponentRouterCreationSupport {

  protected val system: ActorSystem

  protected def createRouter(routes: Map[Class[_] , ActorRef]): ActorRef = {
    system.actorOf(SimpleRouter.props(routes), SimpleRouter.name)
  }
}
