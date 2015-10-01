package com.vita.akka.component.impl

import akka.actor.{Actor, ActorRef, Props}

private [component] object SimpleRouter {
  val name = "SimpleRouter"
  def props(routes: Map[Class[_] , ActorRef]) = Props(classOf[SimpleRouter], routes)
}

/**
 * Simple router of all received messages.
 *
 * Created by Yury on 11.08.2015.
 */
class SimpleRouter(routes: Map[Class[_], ActorRef]) extends Actor {

  def receive: Receive = {
    case message =>
      routes.foreach {
        case (messageType, actor) =>
          if (message.getClass == messageType)
            actor forward message
      }

  }
}
