package com.vita.akka.cake

import akka.actor.{ActorContext, ActorRef, Props}

/**
 * Implementation of ActorCreationSupport for creating child actors from context.
 *
 * Created by Yury on 11.08.2015.
 */
trait ActorContextCreationSupport extends ActorCreationSupport {
  def context: ActorContext
  def createChild(props: Props, name: String): ActorRef = context.actorOf(props, name)
}
