package controllers.tasks.crawlers

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

object CheckQuestDeadline {
  def props(api: DomainAPIComponent#DomainAPI) = {
    Props(classOf[CheckQuestDeadline], api)
  }

  def name = "CheckQuestDeadline"
}

class CheckQuestDeadline(api: DomainAPIComponent#DomainAPI) extends BaseUserCrawler(api) {

  protected def check(user: User) = {
    if (user.questDeadlineReached) {
      api.deadlineQuest(DeadlineQuestRequest(user))
    }
  }

}
