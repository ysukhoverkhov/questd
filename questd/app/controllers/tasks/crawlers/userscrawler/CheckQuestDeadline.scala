package controllers.tasks.crawlers.userscrawler

import akka.actor.Actor
import akka.actor.Props
import play.Logger
import helpers.akka.EasyRestartActor
import controllers.tasks.messages.DoTask
import controllers.domain._
import controllers.domain.app.user._
import models.domain._
import java.util.Date
import logic._
import components.APIAccessor
import components.random.RandomComponent

object CheckQuestDeadline {
  def props(api: DomainAPIComponent#DomainAPI, rand: RandomComponent#Random) = {
    Props(classOf[CheckQuestDeadline], api, rand)
  }

  def name = "CheckQuestDeadline"
}

class CheckQuestDeadline(
    apiPar: DomainAPIComponent#DomainAPI,
    randPar: RandomComponent#Random) extends BaseUserCrawler(apiPar, randPar)  {

  // TODO: remove this crawler.
  protected def check(user: User) = {
//    if (user.questDeadlineReached) {
//      api.deadlineQuest(DeadlineQuestRequest(user))
//    }
  }

}

