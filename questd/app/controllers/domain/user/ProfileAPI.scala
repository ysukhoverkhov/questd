package controllers.domain.user

import models.domain._
import models.store._
import controllers.domain.DomainAPIComponent
import components._
import controllers.domain._
import controllers.domain.helpers.exceptionwrappers._
import logic._


case class GetAllUsersRequest()
case class GetAllUsersResult(users: Iterator[User])

case class ResetCountersRequest(user: User)
case class ResetCountersResult()

private [domain] trait ProfileAPI { this: DBAccessor => 
  
  /**
   * Get iterator for all users.
   */
  def getAllUsers(request: GetAllUsersRequest): ApiResult[GetAllUsersResult] = handleDbException {
    import request._

    OkApiResult(Some(GetAllUsersResult(db.user.all)))
  }

  /**
   * Reset all purchases (quests and themes) overnight.
   */
  def resetCounters(request: ResetCountersRequest): ApiResult[ResetCountersResult] = handleDbException {
    import request._

    db.user.update {
      user.copy(
        profile = user.profile.copy(
          questSolutionContext = user.profile.questSolutionContext.copy(
            purchasedQuest = None,
            numberOfPurchasedQuests = 0),
          questProposalContext = user.profile.questProposalContext.copy(
            purchasedTheme = None,
            numberOfPurchasedThemes = 0),
          questVoteContext = user.profile.questVoteContext.copy(
            reviewingQuestID = None,
            numberOfReviewedQuests = 0)),
        schedules = user.schedules.copy(
          purchases = user.getResetPurchasesTimeout))
    }

    OkApiResult(Some(ResetCountersResult()))
  }

}

