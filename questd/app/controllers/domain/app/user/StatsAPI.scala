package controllers.domain.app.user

import components._
import controllers.domain.helpers._
import controllers.domain.{DomainAPIComponent, _}
import models.domain.user.User

case class ShiftStatsRequest(user: User)
case class ShiftStatsResult()

case class RememberQuestSolvingRequest(user: User, questId: String)
case class RememberQuestSolvingResult()

case class RememberSolutionVotingRequest(user: User, solutionId: String)
case class RememberSolutionVotingResult()

private[domain] trait StatsAPI { this: DomainAPIComponent#DomainAPI with DBAccessor =>

  /**
   * Reset all purchases (quests and themes) overnight.
   */
  def shiftStats(request: ShiftStatsRequest): ApiResult[ShiftStatsResult] = handleDbException {

//    val deltaDays: Double =
//      if (user.stats.lastStatShift.equals(UserStats().lastStatShift))
//        Double.NaN
//      else
//        new Interval(new DateTime(user.stats.lastStatShift), DateTime.now).toDuration.getStandardHours / 24.0
//
//    db.user.updateStats (
//        user.id,
//        user.stats.copy(lastStatShift = new Date()))

    OkApiResult(ShiftStatsResult())
  }

}

