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

object CheckGiveupQuest {
  def props(api: DomainAPIComponent#DomainAPI) = {
    Props(classOf[CheckGiveupQuest], api)
  }

  def name = "CheckGiveupQuest"
}

class CheckGiveupQuest(api: DomainAPIComponent#DomainAPI) extends BaseUserCrawler(api) {

  protected def check(user: User) = {
    if (user.shouldGiveupQuest) {
      api.giveUpQuest(GiveUpQuestRequest(user))
    }
  }

}

