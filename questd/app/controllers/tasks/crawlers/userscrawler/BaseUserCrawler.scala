package controllers.tasks.crawlers.userscrawler

import akka.actor.Actor
import akka.actor.Props
import play.Logger
import helpers.akka.EasyRestartActor
import controllers.tasks.messages.DoTask
import controllers.domain._
import controllers.domain.app.user._
import models.domain._

case class ProcessUser(user: User)

abstract class BaseUserCrawler(api: DomainAPIComponent#DomainAPI) extends EasyRestartActor {

  def receive = {
    case ProcessUser(user: User) => check(user)
    
    case a @ _ => Logger.error("Unknown event received: " + a.toString)
  }

  protected def check(user: User): Unit

}

