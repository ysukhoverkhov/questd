package controllers.tasks.crawlers

import akka.actor.Props
import controllers.domain._
import controllers.tasks.crawlers.userscrawler._
import components.random.RandomComponent


object UsersWeeklyCrawler {
  def props(api: DomainAPIComponent#DomainAPI, rand: RandomComponent#Random) = {
    Props(classOf[UsersWeeklyCrawler], api, rand)
  }

  def name = "UsersWeeklyCrawler"
}

class UsersWeeklyCrawler(api: DomainAPIComponent#DomainAPI, rand: RandomComponent#Random) extends BaseUsersScheduleCrawler(api, rand) {

  protected val userActors = List(
      classOf[ShiftUserStats])
}

