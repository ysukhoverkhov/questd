package controllers.tasks.crawlers.userscrawler

import akka.actor.Props
import components.random.RandomComponent
import controllers.domain._
import controllers.domain.app.user._
import models.domain._

object PopulateTimeLine {
  def props(api: DomainAPIComponent#DomainAPI, rand: RandomComponent#Random) = {
    Props(classOf[PopulateTimeLine], api, rand)
  }

  def name = "PopulateTimeLine"
}

class PopulateTimeLine(
    apiPar: DomainAPIComponent#DomainAPI,
    randPar: RandomComponent#Random) extends BaseUserCrawler(apiPar, randPar)  {

  protected def check(user: User) = {
    // TODO: add here a time check for timeline population.
    api.populateTimeLineWithRandomThings(PopulateTimeLineWithRandomThingsRequest(user))
  }

}

