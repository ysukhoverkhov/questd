package controllers.tasks.crawlers.base

import components.random.RandomComponent
import controllers.domain._
import controllers.domain.app.challenge.{GetAllChallengesForCrawlerRequest, GetAllChallengesForCrawlerResult}
import models.domain.challenge.Challenge
import play.Logger


abstract class BaseChallengeScheduleCrawler(
  api: DomainAPIComponent#DomainAPI,
  rand: RandomComponent#Random) extends BaseScheduleCrawler[Challenge](api, rand) {

    override def allObjects: Iterator[Challenge] = {
      api.getAllChallengesForCrawler(GetAllChallengesForCrawlerRequest()) match {
        case OkApiResult(r: GetAllChallengesForCrawlerResult) =>
          r.challenges

        case _ =>
          Logger.error(s"Unable to get all challenges from database")
          List.empty.iterator
      }
    }
  }

