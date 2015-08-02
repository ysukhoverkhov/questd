package controllers.domain.app.user

import components._
import controllers.domain._
import controllers.domain.app.protocol.ProfileModificationResult._
import controllers.domain.helpers._
import models.domain.solution.Solution
import models.domain.user.User
import models.domain.user.battlerequests.{BattleRequest, BattleRequestStatus}
import models.domain.user.message.{MessageBattleRequestRejected, MessageBattleRequestAccepted}
import models.domain.user.profile.{TaskType, Profile}
import play.Logger

case class ChallengeBattleRequest(
  user: User,
  mySolutionId: String,
  opponentSolutionId: String)
case class ChallengeBattleResult(
  allowed: ProfileModificationResult,
  profile: Option[Profile] = None)

case class GetBattleRequestsRequest(
  user: User)
case class GetBattleRequestsResult(
  allowed: ProfileModificationResult,
  requests: List[BattleRequest])

case class RespondBattleRequestRequest(
  user: User,
  opponentSolutionId: String,
  accept: Boolean)
case class RespondBattleRequestResult(
  allowed: ProfileModificationResult,
  profile: Option[Profile] = None)


private[domain] trait ChallengesAPI { this: DomainAPIComponent#DomainAPI with DBAccessor =>

  /**
   * Challenge someone to jon a battle.
   */
  def challengeBattle(request: ChallengeBattleRequest): ApiResult[ChallengeBattleResult] = handleDbException {
    import request._

    def makeChallenge(mySolution: Solution, opponentSolution: Solution): ApiResult[ChallengeBattleResult] = {
      db.user.addBattleRequest(
        opponentSolution.info.authorId,
        BattleRequest(user.id, opponentSolution.id, mySolution.id, BattleRequestStatus.Requests)) ifSome { opponent =>

        runWhileSome(user) ( { user =>
          db.user.addBattleRequest(
            user.id,
            BattleRequest(
              opponent.id, mySolution.id, opponentSolution.id, BattleRequestStatus.Requested))
        }, { user =>
          // TODO: substract assets for invitation.
          // TODO: test it calls db correctly.
          Some(user)
        }
        ) ifSome { user =>
          {
            makeTask(MakeTaskRequest(user, Some(TaskType.ChallengeBattle)))
          } map { r =>
            OkApiResult(ChallengeBattleResult(OK, Some(r.user.profile)))
          }
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

  /**
   * Get all battle requests we have.
   */
  def getBattleRequests(request: GetBattleRequestsRequest): ApiResult[GetBattleRequestsResult] = handleDbException {
    OkApiResult(GetBattleRequestsResult(
      allowed = OK,
      requests = request.user.battleRequests))
  }

  /**
   * Respond on battle request.
   */
  def respondBattleRequest(request: RespondBattleRequestRequest): ApiResult[RespondBattleRequestResult] = handleDbException {
    import request._

    def createBattleForRequest(br: BattleRequest): ApiResult[RespondBattleRequestResult] = {
      val newStatus = if (accept) BattleRequestStatus.Accepted else BattleRequestStatus.Rejected

      db.user.updateBattleRequest(user.id, br.mySolutionId, br.opponentSolutionId, newStatus.toString) ifSome { user =>
        db.user.updateBattleRequest(
          br.opponentId, br.opponentSolutionId, br.mySolutionId, newStatus.toString) ifSome { opponent =>
          if (accept) {
            db.solution.readById(br.mySolutionId).fold[ApiResult[RespondBattleRequestResult]](
              OkApiResult(RespondBattleRequestResult(OutOfContent))) { mySolution =>
              db.solution.readById(br.opponentSolutionId).fold[ApiResult[RespondBattleRequestResult]](
                OkApiResult(RespondBattleRequestResult(OutOfContent))) { opponentSolution =>
                createBattle(CreateBattleRequest(List(mySolution, opponentSolution))) map {
                  sendMessage(SendMessageRequest(opponent, MessageBattleRequestAccepted(
                    opponentSolutionId = br.mySolutionId)))
                } map {
                  OkApiResult(RespondBattleRequestResult(OK, Some(user.profile)))
                }
              }
            }
          } else {
            // TODO: return money back.
            // TODO: test me.
            sendMessage(SendMessageRequest(opponent, MessageBattleRequestRejected(br.mySolutionId))) map {
              OkApiResult(RespondBattleRequestResult(OK, Some(user.profile)))
            }
          }
        }
      }
    }

    user.battleRequests.find(
      br =>
        (br.status == BattleRequestStatus.Requests) &&
          (br.opponentSolutionId == opponentSolutionId)
    ).fold[ApiResult[RespondBattleRequestResult]] {
      Logger.trace(s"Unable to find battle request with status Requests and opponentSolutionId equal to $opponentSolutionId")
      OkApiResult(RespondBattleRequestResult(OutOfContent))
    } { br =>
      createBattleForRequest(br)
    }
  }
}

