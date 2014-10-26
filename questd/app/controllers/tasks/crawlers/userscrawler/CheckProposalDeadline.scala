package controllers.tasks.crawlers.userscrawler

import akka.actor.Props
import controllers.domain._
import models.domain._
import components.random.RandomComponent

// TODO: remove the crawler.
object CheckProposalDeadline {
  def props(api: DomainAPIComponent#DomainAPI, rand: RandomComponent#Random) = {
    Props(classOf[CheckProposalDeadline], api, rand)
  }

  def name = "CheckProposalDeadline"
}

class CheckProposalDeadline(
    apiPar: DomainAPIComponent#DomainAPI,
    randPar: RandomComponent#Random) extends BaseUserCrawler(apiPar, randPar)  {

  protected def check(user: User) = {
//    if (user.proposalDeadlineReached) {
//      api.deadlineQuestProposal(DeadlineQuestProposalRequest(user))
//    }
  }

}

