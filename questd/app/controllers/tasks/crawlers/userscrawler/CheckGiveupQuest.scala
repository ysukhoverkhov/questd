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

object CheckGiveupQuest {
  def props(api: DomainAPIComponent#DomainAPI) = {
    Props(classOf[CheckGiveupQuest], api)
  }
  
  def name = "CheckGiveupQuest"
}

class CheckGiveupQuest(api: DomainAPIComponent#DomainAPI) extends BaseUserCrawler(api) {

  protected def check(user: User) = {
    if ((user.profile.questContext.takenQuest != None) 
      && (user.profile.questContext.questCooldown.before(new Date()))) {
      api.giveUpQuest(GiveUpQuestRequest(user))
    }
  }

}

