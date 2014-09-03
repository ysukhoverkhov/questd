package controllers.tasks.crawlers

import akka.actor.Actor
import akka.actor.Props
import play.Logger
import helpers.akka.EasyRestartActor
import controllers.tasks.messages.DoTask
import controllers.domain._
import controllers.domain.app.user._
import models.domain._
import controllers.tasks.crawlers.userscrawler._
import components.random.RandomComponent


abstract class BaseUsersScheduleCrawler(api: DomainAPIComponent#DomainAPI, rand: RandomComponent#Random) extends EasyRestartActor {

  protected val userActors: List[Class[_ <: akka.actor.Actor]]

  override def preStart(): Unit = {
    for (clazz <- userActors)
      context.actorOf(Props(clazz, api, rand))
  }

  def receive = {
    case DoTask => {
      Logger.debug("Users crawler " + self.path.toString)

      api.getAllUsers(GetAllUsersRequest()) match {
        case OkApiResult(result) => doCrawl(result.users)
        case _ => Logger.error("Unable to get users from Db to crawl them")
      }
    }
    case a @ _ => Logger.error("Unknown event received: " + a.toString)
  }

  private def doCrawl(users: Iterator[User]) = {
    context.actorSelection("*") ! StartWork
    for (user <- users) {
      context.actorSelection("*") ! ProcessUser(user)
    }
    context.actorSelection("*") ! EndWork
  }

}

