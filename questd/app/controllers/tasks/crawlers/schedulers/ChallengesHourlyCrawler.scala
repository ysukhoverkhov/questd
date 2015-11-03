package controllers.tasks.crawlers.schedulers

import akka.actor.Props
import components.random.RandomComponent
import controllers.domain._
import controllers.tasks.crawlers.base.BaseChallengeScheduleCrawler
import controllers.tasks.crawlers.concrete.challengecrawler.RejectOldChallenges


object ChallengesHourlyCrawler {
  def props(api: DomainAPIComponent#DomainAPI, rand: RandomComponent#Random) = {
    Props(classOf[ChallengesHourlyCrawler], api, rand)
  }

  def name = "ChallengesHourlyCrawler"
}

class ChallengesHourlyCrawler(api: DomainAPIComponent#DomainAPI, rand: RandomComponent#Random) extends BaseChallengeScheduleCrawler(api, rand) {

  protected val actors = List(
      classOf[RejectOldChallenges])
}
