package com.vita.akka.cake

import akka.actor.{ActorContext, ActorRef, Props}

/**
 * Implementation of ActorCreationSupport for creating child actors from context.
 *
 * Created by Yury on 11.08.2015.
 */
trait ActorContextCreationSupport extends ActorCreationSupport {
  protected def context: ActorContext
  protected def createActor(props: Props, name: String): ActorRef = context.actorOf(props, name)
}
