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

private[domain] trait StatsAPI { this: DBAccessor =>

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
          proposalsReviewed = 0,
          proposalsReviewedPerDay = if (deltaDays == Double.NaN) 0 else user.stats.proposalsReviewed / deltaDays,
          proposalsAccepted = 0,
          proposalsAcceptedPerDay = if (deltaDays == Double.NaN) 0 else user.stats.proposalsAccepted / deltaDays
        ))

    OkApiResult(Some(ShiftStatsResult()))
  }

  /**
   * Shifts user history one day forward.
   */
  def shiftHistory(request: ShiftHistoryRequest): ApiResult[ShiftHistoryResult] = handleDbException {
    import request._
    
    // TODO: take me from settings in admin.
    val historyDepth = 15
    
    db.user.addFreshDayToHistory(user.id)
    clearOldHistory(user)
    
    def clearOldHistory(u: User): User = {
	  if (u.history.votedQuestProposalIds.length >= historyDepth) {
	    clearOldHistory(db.user.removeLastDayFromHistory(u.id).get)
      } else {
        u
      }
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

