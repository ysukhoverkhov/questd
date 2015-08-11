package com.vita.akka

import akka.actor.Actor

/**
 * Helper for akka actors.
 */
trait EasyRestartActor { this: Actor =>

  override def preStart(): Unit = ()

  override def postRestart(reason: Throwable): Unit = ()

  override def preRestart(reason: Throwable, message: Option[Any]): Unit = {
    postStop()
  }
}
