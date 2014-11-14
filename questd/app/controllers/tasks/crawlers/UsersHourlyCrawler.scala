package controllers.tasks.crawlers

import akka.actor.Props
import controllers.domain._
import controllers.tasks.crawlers.userscrawler._
import components.random.RandomComponent

import models.domain._


object UsersHourlyCrawler {
  def props(api: DomainAPIComponent#DomainAPI, rand: RandomComponent#Random) = {
    Props(classOf[UsersHourlyCrawler], api, rand)
  }

  def name = "UsersHourlyCrawler"
}

class UsersHourlyCrawler(api: DomainAPIComponent#DomainAPI, rand: RandomComponent#Random) extends BaseUsersScheduleCrawler(api, rand) {

  // TODO: check remove unneccessary actors.
  protected val userActors = List(
      classOf[ResetPurchasesAtNight],
      classOf[ResetTasksAtNight],
      classOf[CheckShiftDailyResult],
      classOf[PopulateTimeLine])
}
