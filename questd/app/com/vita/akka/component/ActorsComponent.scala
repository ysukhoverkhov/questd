package com.vita.akka.component

import akka.actor.{Actor, ActorSystem}

/**
 * Generic interface for actor components.
 *
 * Created by Yury on 01.10.2015.
 */
abstract class ActorsComponent(
  private val system: ActorSystem) {


  /**
   * Root actor for working out all requests.
   *
   * @return Actor to workout all requests.
   */
  def actor: Actor
}
