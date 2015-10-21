package controllers.tasks.crawlers.concrete.challengecrawler

import akka.actor.Props
import components.random.RandomComponent
import controllers.domain._
import controllers.domain.app.challenge.AutoRejectChallengeRequest
import controllers.tasks.crawlers.base.BaseCrawler
import models.domain.challenge.Challenge

object RejectOldChallenges {
  def props(api: DomainAPIComponent#DomainAPI, rand: RandomComponent#Random) = {
    Props(classOf[RejectOldChallenges], api, rand)
  }

  def name = "RejectOldChallenges"
}

class RejectOldChallenges(
    apiPar: DomainAPIComponent#DomainAPI,
    randPar: RandomComponent#Random) extends BaseCrawler[Challenge](apiPar, randPar)  {

  protected def check(challenge: Challenge) = {
    if (challenge.shouldBeAutoRejected)
      api.autoRejectChallenge(AutoRejectChallengeRequest(challenge))
  }
}

