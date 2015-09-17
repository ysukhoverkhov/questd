package controllers.tasks.crawlers.schedulers

import akka.actor.Props
import components.random.RandomComponent
import controllers.domain._
import controllers.tasks.crawlers.base.BaseSolutionScheduleCrawler
import controllers.tasks.crawlers.concrete.solutioncrawler.TryCreateBattleForSolution


object SolutionsHourlyCrawler {
  def props(api: DomainAPIComponent#DomainAPI, rand: RandomComponent#Random) = {
    Props(classOf[SolutionsHourlyCrawler], api, rand)
  }

  def name = "SolutionsHourlyCrawler"
}

class SolutionsHourlyCrawler(api: DomainAPIComponent#DomainAPI, rand: RandomComponent#Random) extends BaseSolutionScheduleCrawler(api, rand) {

  protected val actors = List(
      classOf[TryCreateBattleForSolution])
}
