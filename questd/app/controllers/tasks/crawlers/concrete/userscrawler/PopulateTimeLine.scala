package controllers.tasks.crawlers.concrete.userscrawler

import java.util.Date

import akka.actor.Props
import components.random.RandomComponent
import controllers.domain._
import controllers.domain.app.user._
import controllers.tasks.crawlers.base.BaseCrawler
import models.domain._
import models.domain.user.User

object PopulateTimeLine {
  def props(api: DomainAPIComponent#DomainAPI, rand: RandomComponent#Random) = {
    Props(classOf[PopulateTimeLine], api, rand)
  }

  def name = "PopulateTimeLine"
}

class PopulateTimeLine(
    apiPar: DomainAPIComponent#DomainAPI,
    randPar: RandomComponent#Random) extends BaseCrawler[User](apiPar, randPar)  {

  protected def check(user: User) = {
    if (user.schedules.timeLine.before(new Date())) {
      api.populateTimeLineWithRandomThings(PopulateTimeLineWithRandomThingsRequest(user))
    }
  }
}

