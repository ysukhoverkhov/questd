package controllers.domain.app.user

import components._
import controllers.domain._
import controllers.domain.app.protocol.ProfileModificationResult._
import controllers.domain.helpers._
import models.domain.solution.Solution
import models.domain.user.User
import models.domain.user.battlerequests.{BattleRequest, BattleRequestStatus}
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
  // TODO: test it calls db correctly.
  def challengeBattle(request: ChallengeBattleRequest): ApiResult[ChallengeBattleResult] = handleDbException {
    import request._

    def makeChallenge(mySolution: Solution, opponentSolution: Solution): ApiResult[ChallengeBattleResult] = {
      db.user.addBattleRequest(
        opponentSolution.info.authorId,
        BattleRequest(user.id, opponentSolution.id, mySolution.id, BattleRequestStatus.Requests)) ifSome { opponent =>

        db.user.addBattleRequest(
          user.id,
          BattleRequest(
            opponent.id, mySolution.id, opponentSolution.id, BattleRequestStatus.Requested)) ifSome { user =>

          // TODO: substract assets for invitation.
          OkApiResult(ChallengeBattleResult(OK, Some(user.profile)))
        }
      }
    }

    db.solution.readById(mySolutionId).fold[ApiResult[ChallengeBattleResult]](OkApiResult(ChallengeBattleResult(OutOfContent))) { mySolution =>
      db.solution.readById(opponentSolutionId).fold[ApiResult[ChallengeBattleResult]](OkApiResult(ChallengeBattleResult(OutOfContent))) { opponentSolution =>
        user.canChallengeBattle(mySolution, opponentSolution) match {
          case OK =>
            makeChallenge(mySolution, opponentSolution)
          case reason =>
            OkApiResult(ChallengeBattleResult(reason))
        }
      }
    }
  }
}

