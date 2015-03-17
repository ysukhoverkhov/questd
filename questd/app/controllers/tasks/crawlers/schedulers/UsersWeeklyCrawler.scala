package controllers.tasks.crawlers.schedulers

import akka.actor.Props
import components.random.RandomComponent
import controllers.domain._
import controllers.tasks.crawlers.base.BaseUsersScheduleCrawler
import controllers.tasks.crawlers.concrete.userscrawler._


object UsersWeeklyCrawler {
  def props(api: DomainAPIComponent#DomainAPI, rand: RandomComponent#Random) = {
    Props(classOf[UsersWeeklyCrawler], api, rand)
  }

  def name = "UsersWeeklyCrawler"
}

class UsersWeeklyCrawler(api: DomainAPIComponent#DomainAPI, rand: RandomComponent#Random) extends BaseUsersScheduleCrawler(api, rand) {

  protected val actors = List(
      classOf[ShiftUserStats])
}

