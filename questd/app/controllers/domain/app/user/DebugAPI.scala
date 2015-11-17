package controllers.domain.app.user

import java.util.Date

import components._
import controllers.domain.app.battle.UpdateBattleStateRequest
import controllers.domain.app.protocol.CommonCode
import controllers.domain.helpers._
import controllers.domain.{DomainAPIComponent, _}
import models.domain.battle.BattleStatus
import models.domain.user._
import models.domain.user.dailyresults.DailyResult
import models.domain.user.profile.{Analytics, Profile}
import models.domain.user.stats.UserStats

object SetDebugCode extends Enumeration with CommonCode
case class SetDebugRequest(user: User, debug: String)
case class SetDebugResult(allowed: SetDebugCode.Value, profile: Option[Profile] = None)

case class SetLevelDebugRequest(user: User, level: Int)
case class SetLevelDebugResult(user: User)

object ResetProfileDebugCode extends Enumeration with CommonCode
case class ResetProfileDebugRequest(user: User)
case class ResetProfileDebugResult(allowed: ResetProfileDebugCode.Value, profile: Option[Profile] = None)

object ResolveAllBattlesCode extends Enumeration with CommonCode
case class ResolveAllBattlesRequest(user: User)
case class ResolveAllBattlesResult(allowed: ResolveAllBattlesCode.Value, profile: Option[Profile] = None)

private[domain] trait DebugAPI { this: DomainAPIComponent#DomainAPI with DBAccessor =>

  /**
   * Updates debug string in user profile.
   */
  def setDebug(request: SetDebugRequest): ApiResult[SetDebugResult] = handleDbException {
    import request._

    db.user.setDebug(user.id, debug) ifSome { v =>
      OkApiResult(SetDebugResult(SetDebugCode.OK, Some(v.profile)))
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


  /**
   * Resets money and tutorial and level.
   */
  def resetProfileDebug(request: ResetProfileDebugRequest): ApiResult[ResetProfileDebugResult] = handleDbException {
    import request._

    db.user.update(
      user.copy(
        stats = UserStats(),
        following = List.empty,
        followers = List.empty,
        friends = List.empty,
        profile = user.profile.copy(
          analytics = Analytics()
        )
      )
    )

    {
      adjustAssets(AdjustAssetsRequest(user, -user.profile.assets))
    } map { r =>
      setLevelDebug(SetLevelDebugRequest(r.user, 1))
    } map { r =>
      resetTutorial(ResetTutorialRequest(r.user))
    } map { r =>
      OkApiResult(ResetProfileDebugResult(ResetProfileDebugCode.OK, r.profile))
    }
  }

  /**
   * Makes all battles for user resolved.
   */
  def resolveAllBattles(request: ResolveAllBattlesRequest): ApiResult[ResolveAllBattlesResult] = handleDbException {

    db.battle.all.foreach { battle =>
      if (battle.info.status == BattleStatus.Fighting)
        api.updateBattleState(UpdateBattleStateRequest(battle.copy(info = battle.info.copy(voteEndDate = new Date()))))
    }

    OkApiResult(ResolveAllBattlesResult(ResolveAllBattlesCode.OK, Some(request.user.profile)))
  }
}

