package controllers.tasks.crawlers.userscrawler

import akka.actor.Actor
import akka.actor.Props
import play.Logger
import helpers.akka.EasyRestartActor
import controllers.tasks.messages.DoTask
import controllers.domain._
import controllers.domain.app.user._
import models.domain._
import logic._
import java.util.Date
import components.APIAccessor
import components.random.RandomComponent

object CheckProposalDeadline {
  def props(api: DomainAPIComponent#DomainAPI) = {
    Props(classOf[CheckProposalDeadline], api)
  }

  def name = "CheckProposalDeadline"
}

class CheckProposalDeadline(
    apiPar: DomainAPIComponent#DomainAPI,
    randPar: RandomComponent#Random) extends BaseUserCrawler(apiPar, randPar)  {

  protected def check(user: User) = {
    if (user.proposalDeadlineReached) {
      api.deadlineQuestProposal(DeadlineQuestProposalRequest(user))
    }
  }

}

