package controllers.tasks.crawlers.concrete.battlecrawler

import akka.actor.Props
import components.random.RandomComponent
import controllers.domain._
import controllers.domain.app.battle.UpdateBattleStateRequest
import controllers.tasks.crawlers.base.BaseCrawler
import controllers.tasks.crawlers.concrete.userscrawler.ShiftDailyResult
import models.domain._
import play.Logger

//import org.joda.time.DateTime
import com.github.nscala_time.time.Imports._

object ResolveBattle {
  def props(api: DomainAPIComponent#DomainAPI, rand: RandomComponent#Random) = {
    Props(classOf[ShiftDailyResult], api, rand)
  }

  def name = "ResolveBattle"
}

class ResolveBattle(
    apiPar: DomainAPIComponent#DomainAPI,
    randPar: RandomComponent#Random) extends BaseCrawler[Battle](apiPar, randPar)  {

  protected def check(battle: Battle) = {
    Logger.error("error")
    Logger.debug("debug")
    // it gives us only batles in fighting state so no need in checking the state.
    if (DateTime.now >= new DateTime(battle.info.voteEndDate)) {
      api.updateBattleState(UpdateBattleStateRequest(battle))
    }
  }
}

