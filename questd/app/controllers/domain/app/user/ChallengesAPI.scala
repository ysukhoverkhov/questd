package controllers.domain.app.user

import components._
import controllers.domain._
import controllers.domain.app.protocol.ProfileModificationResult._
import controllers.domain.helpers._
import models.domain.user.User
import models.domain.user.profile.Profile


case class ChallengeBattleRequest(
  user: User,
  mySolutionId: String,
  opponentSolutionId: String)
case class ChallengeBattleResult(
  allowed: ProfileModificationResult,
  profile: Option[Profile] = None)

private[domain] trait ChallengesAPI { this: DomainAPIComponent#DomainAPI with DBAccessor =>

  /**
   * Challenge someone to jon a battle.
   */
  def challengeBattle(request: ChallengeBattleRequest): ApiResult[ChallengeBattleResult] = handleDbException {

    // TODO: implement me.

    OkApiResult(ChallengeBattleResult(OK, Some(request.user.profile)))
  }

}

