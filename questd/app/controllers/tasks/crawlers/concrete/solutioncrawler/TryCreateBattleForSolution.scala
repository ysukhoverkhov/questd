package controllers.tasks.crawlers.concrete.solutioncrawler

import akka.actor.Props
import components.random.RandomComponent
import controllers.domain._
import controllers.domain.app.user.TryCreateBattleRequest
import controllers.tasks.crawlers.base.BaseCrawler
import models.domain.solution.Solution

object TryCreateBattleForSolution {
  def props(api: DomainAPIComponent#DomainAPI, rand: RandomComponent#Random) = {
    Props(classOf[TryCreateBattleForSolution], api, rand)
  }

  def name = "TryCreateBattleForSolution"
}

class TryCreateBattleForSolution(
    apiPar: DomainAPIComponent#DomainAPI,
    randPar: RandomComponent#Random) extends BaseCrawler[Solution](apiPar, randPar)  {

  protected def check(solution: Solution) = {
    api.tryCreateBattle(TryCreateBattleRequest(solution))
  }
}

