package com.vita.akka.component

import akka.actor.Actor
import com.vita.akka.cake.ActorContextCreationSupport

/**
 * Support for creating routers for messages.
 *
 * Created by Yury on 01.10.2015.
 */
trait ComponentRouterSimpleCreationSupport extends ActorContextCreationSupport{

  protected def createRouter(routes: Map[Any , Actor]): Actor
}
