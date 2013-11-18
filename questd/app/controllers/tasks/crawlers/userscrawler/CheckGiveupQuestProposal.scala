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

object CheckGiveupQuestProposal {
  def props(api: DomainAPIComponent#DomainAPI) = {
    Props(classOf[UsersHourlCrawler], api)
  }
  
  def name = "CheckGiveupQuestProposal"
}

class CheckGiveupQuestProposal(api: DomainAPIComponent#DomainAPI) extends EasyRestartActor {

  def receive = {
    case ProcessUser(user: User) => check(user)
    
    case a @ _ => Logger.error("Unknown event received: " + a.toString)
  }

  private def check(user: User) = {
    if ((user.profile.questProposalContext.takenTheme != None) 
      && (user.profile.questProposalContext.questProposalCooldown.before(new Date()))) {
      api.giveUpQuestProposal(GiveUpQuestProposalRequest(user))
    }
  }

}

