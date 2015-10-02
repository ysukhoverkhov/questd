package com.vita.akka.cake

import akka.actor.{ActorRef, ActorSystem, Props}

/**
 * Implementation of ActorCreationSupport for creating root actors from system.
 *
 * Created by Yury on 11.08.2015.
 */
trait ActorSystemCreationSupport extends ActorCreationSupport {
  protected def system: ActorSystem
  protected def createActor(props: Props, name: String): ActorRef = system.actorOf(props, name)
}
