package controllers.domain.app.user

import models.domain._
import models.store._
import controllers.domain.DomainAPIComponent
import components._
import controllers.domain._
import controllers.domain.helpers.exceptionwrappers._
import logic._
import java.util.Date
import org.joda.time.DateTime
import org.joda.time.Interval

case class ShiftStatsRequest(user: User)
case class ShiftStatsResult()

case class ShiftHistoryRequest(user: User)
case class ShiftHistoryResult()

case class RememberProposalVotingRequest(user: User, proposalId: String)
case class RememberProposalVotingResult()

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
        (new Interval(new DateTime(user.stats.lastStatShift), DateTime.now)).toDuration().getStandardHours() / 24.0

    db.user.updateStats (
        user.id, 
        UserStats (
          lastStatShift = new Date(),
          questsReviewed = 0,
          questsAccepted = 0,
          questsReviewedPast = (user.stats.questsReviewedPast * 3) / 4 + user.stats.questsReviewed,
          questsAcceptedPast = (user.stats.questsAcceptedPast * 3) / 4 + user.stats.questsAccepted,
          proposalsVoted = 0,
          proposalsVotedPerDay = if (deltaDays == Double.NaN) 0 else user.stats.proposalsVoted / deltaDays,
          proposalsLiked = 0,
          proposalsLikedPerDay = if (deltaDays == Double.NaN) 0 else user.stats.proposalsLiked / deltaDays
        ))

    OkApiResult(Some(ShiftStatsResult()))
  }

  /**
   * Shifts user history one day forward.
   */
  def shiftHistory(request: ShiftHistoryRequest): ApiResult[ShiftHistoryResult] = handleDbException {
    import request._
    
    val daysHistoryDepth = config(ConfigParams.UserHistoryDays).toInt
    val themesHistoryDepth: Int = Math.max(1, Math.round(db.theme.count * config(ConfigParams.FavoriteThemesShare).toFloat))
    
    db.user.addFreshDayToHistory(user.id)
    clearOldDaysHistory(user)
    clearOldThemesHistory(user)
    
    def clearOldDaysHistory(u: User): User = {
	  if (u.history.votedQuestProposalIds.length > daysHistoryDepth) {
	    clearOldDaysHistory(db.user.removeLastDayFromHistory(u.id).get)
      } else {
        u
      }
    }
    
    def clearOldThemesHistory(u: User): User = {
      if (u.history.selectedThemeIds.length > themesHistoryDepth)
        db.user.removeLastThemesFromHistory(u.id, u.history.selectedThemeIds.length - themesHistoryDepth).get
      else
        u
    }
    
    OkApiResult(Some(ShiftHistoryResult()))
  }
  
  /**
   * Remember voted proposal
   */
  def rememberProposalVotingInHistory(request: RememberProposalVotingRequest): ApiResult[RememberProposalVotingResult] = handleDbException {
    import request._
    
    db.user.rememberProposalVotingInHistory(user.id, request.proposalId)
    
    OkApiResult(Some(RememberProposalVotingResult()))
  }
  
  /**
   * Remember solved quest
   */
  def rememberQuestSolvingInHistory(request: RememberQuestSolvingRequest): ApiResult[RememberQuestSolvingResult] = handleDbException {
    import request._
    
    db.user.rememberQuestSolvingInHistory(user.id, request.questId)
    
    OkApiResult(Some(RememberQuestSolvingResult()))
  }
  
  /**
   * Remember voted solution
   */
  def rememberSolutionVotingInHistory(request: RememberSolutionVotingRequest): ApiResult[RememberSolutionVotingResult] = handleDbException {
    import request._
    
    db.user.rememberSolutionVotingInHistory(user.id, request.solutionId)
    
    OkApiResult(Some(RememberSolutionVotingResult()))
  }
  
  
  
}

