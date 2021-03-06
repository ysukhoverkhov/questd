package controllers.tasks.crawlers.base

import akka.actor.{Actor, Props}
import com.vita.akka.EasyRestartActor
import components.random.RandomComponent
import controllers.tasks.messages.DoTask
import controllers.domain._
import play.Logger


abstract class BaseScheduleCrawler[T](api: DomainAPIComponent#DomainAPI, rand: RandomComponent#Random) extends Actor with EasyRestartActor {

  protected val actors: List[Class[_ <: akka.actor.Actor]]

  override def preStart(): Unit = {
    for (clazz <- actors)
      context.actorOf(Props(clazz, api, rand))
  }

  def receive = {
    case DoTask =>
      Logger.debug("Crawler " + self.path.toString)
      doCrawl(allObjects)
    case a @ AnyRef => Logger.error(s"Unknown event received: ${a.toString}")
  }

  def allObjects: Iterator[T]

  private def doCrawl(objects: Iterator[T]) = {
    context.actorSelection("*") ! StartWork
    for (user <- objects) {
      context.actorSelection("*") ! Process(user)
    }
    context.actorSelection("*") ! EndWork
  }

}

