package com.vita.akka.component

import akka.actor.Actor

/**
 * Support for creating routers for messages.
 *
 * Created by Yury on 01.10.2015.
 */
trait ComponentRouterCreationSupport {

  protected def createRouter(routes: Map[Any , Actor]): Actor
}
