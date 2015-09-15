package com.vita.akka.cake

import akka.actor.{ActorRef, Props}

/**
 * Trait for all interfaces what rules actor creation. We may want to substitute actor creation for testing.
 *
 * Created by Yury on 11.08.2015.
 */
trait ActorCreationSupport {
  def createChild(props: Props, name: String): ActorRef
}
