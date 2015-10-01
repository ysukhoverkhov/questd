package com.vita.akka.component

import akka.actor.{Actor, ActorSystem}

/**
 * Generic interface for actor components.
 *
 * Created by Yury on 01.10.2015.
 */
abstract class RoutedActorsComponent(
  private val system: ActorSystem) extends ActorsComponent(system) with ComponentRouterCreationSupport {

  def routes: Map[Any, Actor]

  val actor = createRouter(routes)

}
