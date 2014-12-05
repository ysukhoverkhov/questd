package controllers.tasks.crawlers.base

import components.random.RandomComponent
import controllers.domain._
import controllers.domain.app.battle.{GetAllBattlesInternalRequest, GetAllBattlesInternalResult}
import models.domain.Battle
import play.Logger


abstract class BaseBattleScheduleCrawler(
   api: DomainAPIComponent#DomainAPI,
   rand: RandomComponent#Random) extends BaseScheduleCrawler[Battle](api, rand) {

      override def allObjects: Iterator[Battle] = {
        api.getAllBattlesInternal(GetAllBattlesInternalRequest()) match {
          case OkApiResult(r: GetAllBattlesInternalResult) =>
            r.battles

          case _ =>
            Logger.error(s"Unable to et all users from database")
            List().iterator
        }
      }
    }

