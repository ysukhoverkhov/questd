package controllers.domain.user

import models.domain._
import models.store._
import controllers.domain.DomainAPIComponent
import components._
import controllers.domain._
import controllers.domain.helpers.exceptionwrappers._
import logic._


case class ShiftStatsRequest(user: User)
case class ShiftStatsResult()

private [domain] trait StatsAPI { this: DBAccessor => 
  
  /**
   * Reset all purchases (quests and themes) overnight.
   */
  def shiftStats(request: ShiftStatsRequest): ApiResult[ShiftStatsResult] = handleDbException {
    import request._

    db.user.update {
      user.copy(
        stats = user.stats.copy(
  questsReviewed = 0,
  questsAccepted = 0,
  questsReviewedPast = (user.stats.questsReviewedPast * 3) / 4 + user.stats.questsReviewed,
  questsAcceptedPast = (user.stats.questsAcceptedPast * 3) / 4 + user.stats.questsAccepted))
    }

    OkApiResult(Some(ShiftStatsResult()))
  }

}

