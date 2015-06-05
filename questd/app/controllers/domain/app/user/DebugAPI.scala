package controllers.domain.app.user

import components._
import controllers.domain.app.protocol.ProfileModificationResult._
import controllers.domain.helpers._
import controllers.domain.{DomainAPIComponent, _}
import models.domain.user._
import models.domain.user.dailyresults.DailyResult

case class SetDebugRequest(user: User, debug: String)
case class SetDebugResult(allowed: ProfileModificationResult, profile: Option[Profile] = None)

case class SetLevelDebugRequest(user: User, level: Int)
case class SetLevelDebugResult(user: User)

private[domain] trait DebugAPI { this: DomainAPIComponent#DomainAPI with DBAccessor =>

  /**
   * Updates debug string in user profile.
   */
  def setDebug(request: SetDebugRequest): ApiResult[SetDebugResult] = handleDbException {
    import request._

    db.user.setDebug(user.id, debug) ifSome { v =>
      OkApiResult(SetDebugResult(OK, Some(v.profile)))
    }

  }

  /**
   * Debug api for setting level of user.
   */
  def setLevelDebug(request: SetLevelDebugRequest): ApiResult[SetLevelDebugResult] = handleDbException {
    import request._

    val newLevel = user.copy(
      profile = user.profile.copy(
        publicProfile = user.profile.publicProfile.copy(
          level = level
        )
      ))

    val userWithNewRights = newLevel.copy(
      profile = newLevel.profile.copy(
        rights = newLevel.calculateRights,
        ratingToNextLevel = newLevel.ratingToNextLevel
      ),
      privateDailyResults = List(
        DailyResult(
          newLevel.getStartOfCurrentDailyResultPeriod)
      ))
    db.user.update(userWithNewRights)

    OkApiResult(SetLevelDebugResult(userWithNewRights))
  }
}

