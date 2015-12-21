package controllers.tasks.crawlers.base

import components.random.RandomComponent
import controllers.domain._
import controllers.domain.app.solution.{GetAllSolutionsInternalRequest, GetAllSolutionsInternalResult}
import models.domain.solution.Solution
import play.Logger


abstract class BaseSolutionScheduleCrawler(
   api: DomainAPIComponent#DomainAPI,
   rand: RandomComponent#Random) extends BaseScheduleCrawler[Solution](api, rand) {

      override def allObjects: Iterator[Solution] = {
        api.getAllSolutionsInternal(GetAllSolutionsInternalRequest()) match {
          case OkApiResult(r: GetAllSolutionsInternalResult) =>
            r.solutions

          case _ =>
            Logger.error(s"Unable to get all solutions from database")
            Iterator.empty
        }
      }
    }

