package controllers.tasks.crawlers.schedulers

import akka.actor.Props
import components.random.RandomComponent
import controllers.domain._
import controllers.tasks.crawlers.base.BaseSolutionScheduleCrawler
import controllers.tasks.crawlers.concrete.solutioncrawler.UpdateSolutionQualityCurve


object SolutionsWeeklyCrawler {
  def props(api: DomainAPIComponent#DomainAPI, rand: RandomComponent#Random) = {
    Props(classOf[SolutionsWeeklyCrawler], api, rand)
  }

  def name = "SolutionsWeeklyCrawler"
}

class SolutionsWeeklyCrawler(api: DomainAPIComponent#DomainAPI, rand: RandomComponent#Random) extends BaseSolutionScheduleCrawler(api, rand) {

  protected val actors = List(
      classOf[UpdateSolutionQualityCurve])
}
