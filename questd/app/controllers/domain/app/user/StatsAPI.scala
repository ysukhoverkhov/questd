package controllers.domain.app.user

import models.domain._
import controllers.domain.DomainAPIComponent
import components._
import controllers.domain._
import controllers.domain.helpers._
import java.util.Date
import org.joda.time.DateTime
import org.joda.time.Interval

case class ShiftStatsRequest(user: User)
case class ShiftStatsResult()

case class ShiftHistoryRequest(user: User)
case class ShiftHistoryResult()

case class RememberQuestSolvingRequest(user: User, questId: String)
case class RememberQuestSolvingResult()

case class RememberSolutionVotingRequest(user: User, solutionId: String)
case class RememberSolutionVotingResult()

private[domain] trait StatsAPI { this: DomainAPIComponent#DomainAPI with DBAccessor =>

  /**
   * Reset all purchases (quests and themes) overnight.
   */
  def shiftStats(request: ShiftStatsRequest): ApiResult[ShiftStatsResult] = handleDbException {
    import request._

    val deltaDays: Double =
      if (user.stats.lastStatShift.equals(UserStats().lastStatShift))
        Double.NaN
      else
        new Interval(new DateTime(user.stats.lastStatShift), DateTime.now).toDuration.getStandardHours / 24.0

    db.user.updateStats (
        user.id,
        UserStats (
          lastStatShift = new Date(),
          proposalsVoted = 0,
          proposalsVotedPerDay = if (deltaDays == Double.NaN) 0 else user.stats.proposalsVoted / deltaDays,
          proposalsLiked = 0,
          proposalsLikedPerDay = if (deltaDays == Double.NaN) 0 else user.stats.proposalsLiked / deltaDays
        ))

    OkApiResult(ShiftStatsResult())
  }

  /**
   * Shifts user history one day forward.
   */
  def shiftHistory(request: ShiftHistoryRequest): ApiResult[ShiftHistoryResult] = handleDbException {
    import request._

    val daysHistoryDepth = config(ConfigParams.UserHistoryDays).toInt
    val themesHistoryDepth: Int = Math.max(1, Math.round(db.theme.count * config(ConfigParams.FavoriteThemesShare).toFloat))

    db.user.addFreshDayToHistory(user.id)
    clearOldThemesSelectedHistory(user)
    clearOldQuestThemesHistory(user)

    def clearOldThemesSelectedHistory(u: User): Option[User] = {
      if (u.history.selectedThemeIds.length > themesHistoryDepth)
        db.user.removeLastThemesFromHistory(u.id, u.history.selectedThemeIds.length - themesHistoryDepth)
      else
        Some(u)
    }

    def clearOldQuestThemesHistory(u: User): Option[User] = {
      if (u.history.themesOfSelectedQuests.length > themesHistoryDepth)
        db.user.removeLastQuestThemesFromHistory(u.id, u.history.themesOfSelectedQuests.length - themesHistoryDepth)
      else
        Some(u)
    }

    OkApiResult(ShiftHistoryResult())
  }

}

