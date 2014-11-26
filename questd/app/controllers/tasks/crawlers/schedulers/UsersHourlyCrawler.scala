package controllers.tasks.crawlers.schedulers

import akka.actor.Props
import components.random.RandomComponent
import controllers.domain._
import controllers.tasks.crawlers.base.BaseUsersScheduleCrawler
import controllers.tasks.crawlers.concrete.userscrawler._


object UsersHourlyCrawler {
  def props(api: DomainAPIComponent#DomainAPI, rand: RandomComponent#Random) = {
    Props(classOf[UsersHourlyCrawler], api, rand)
  }

  def name = "UsersHourlyCrawler"
}

class UsersHourlyCrawler(api: DomainAPIComponent#DomainAPI, rand: RandomComponent#Random) extends BaseUsersScheduleCrawler(api, rand) {

  protected val actors = List(
      classOf[ResetPurchasesAtNight],
      classOf[ResetTasksAtNight],
      classOf[ShiftDailyResult],
      classOf[PopulateTimeLine])
}
