package controllers.tasks.crawlers

import akka.actor.Actor
import akka.actor.Props
import play.Logger
import helpers.akka.EasyRestartActor
import controllers.tasks.messages.DoTask
import controllers.domain._
import controllers.domain.user._
import models.domain._
import java.util.Date

abstract class BaseUserCrawler(api: DomainAPIComponent#DomainAPI) extends EasyRestartActor {

  def receive = {
    case ProcessUser(user: User) => check(user)
    
    case a @ _ => Logger.error("Unknown event received: " + a.toString)
  }

  protected def check(user: User): Unit

}

