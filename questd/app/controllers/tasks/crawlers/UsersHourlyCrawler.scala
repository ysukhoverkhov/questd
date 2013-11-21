package controllers.tasks.crawlers

import akka.actor.Actor
import akka.actor.Props
import play.Logger
import helpers.akka.EasyRestartActor
import controllers.tasks.messages.DoTask
import controllers.domain._
import controllers.domain.user._

import models.domain._


case class ProcessUser(user: User)

object UsersHourlCrawler {
  def props(api: DomainAPIComponent#DomainAPI) = {
    Props(classOf[UsersHourlCrawler], api)
  }

  def name = "UsersHourlCrawler"
}

class UsersHourlCrawler(api: DomainAPIComponent#DomainAPI) extends EasyRestartActor {

  private val userActors = List(
      classOf[CheckGiveupQuestProposal],
      classOf[CheckGiveupQuest],
      classOf[ResetPurchasesAtNight])

  override def preStart(): Unit = {
    for (clazz <- userActors)
      context.actorOf(Props(clazz, api))
  }

  def receive = {
    case DoTask => {
      Logger.debug("Hourly users crawler " + self.path.toString)

      api.getAllUsers(GetAllUsersRequest()) match {
        case OkApiResult(Some(result)) => doCrawl(result.users)
        case _ => Logger.error("Unable to get users from Db to crawl them")
      }
    }
    case a @ _ => Logger.error("Unknown event received: " + a.toString)
  }

  private def doCrawl(users: Iterator[User]) = {
    for (user <- users) {
      context.actorSelection("*") ! ProcessUser(user)
    }
  }

}

