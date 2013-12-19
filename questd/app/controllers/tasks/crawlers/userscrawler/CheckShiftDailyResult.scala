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

object CheckShiftDailyResult {
  def props(api: DomainAPIComponent#DomainAPI) = {
    Props(classOf[CheckShiftDailyResult], api)
  }
  
  def name = "CheckShiftDailyResult"
}

class CheckShiftDailyResult(api: DomainAPIComponent#DomainAPI) extends BaseUserCrawler(api) {

  protected def check(user: User) = {
    if ((user.profile.questSolutionContext.takenQuest != None) 
      && (user.profile.questSolutionContext.questCooldown.before(new Date()))) {
      api.giveUpQuest(GiveUpQuestRequest(user))
    }
  }

}

