package controllers.tasks.crawlers.schedulers

import akka.actor.Props
import components.random.RandomComponent
import controllers.domain._
import controllers.tasks.crawlers.base.BaseBattleScheduleCrawler
import controllers.tasks.crawlers.concrete.battlecrawler.ResolveBattle


object BattlesHourlyCrawler {
  def props(api: DomainAPIComponent#DomainAPI, rand: RandomComponent#Random) = {
    Props(classOf[BattlesHourlyCrawler], api, rand)
  }

  def name = "BattlesHourlyCrawler"
}

class BattlesHourlyCrawler(api: DomainAPIComponent#DomainAPI, rand: RandomComponent#Random) extends BaseBattleScheduleCrawler(api, rand) {

  protected val actors = List(
      classOf[ResolveBattle])
}
