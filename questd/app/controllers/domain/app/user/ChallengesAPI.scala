package controllers.domain.app.user

import components._
import controllers.domain._
import controllers.domain.app.protocol.ProfileModificationResult._
import controllers.domain.helpers._
import models.domain.challenge.{ChallengeStatus, Challenge}
import models.domain.solution.Solution
import models.domain.user.User
import models.domain.user.message.{MessageBattleRequestRejected, MessageBattleRequestAccepted}
import models.domain.user.profile.{TaskType, Profile}
import play.Logger

case class MakeChallengeRequest(
  user: User,
  mySolutionId: String,
  opponentSolutionId: String)
case class MakeChallengeResult(
  allowed: ProfileModificationResult,
  profile: Option[Profile] = None)

case class GetMyChallengesRequest(
  user: User)
case class GetBattleRequestsResult(
  allowed: ProfileModificationResult,
  requests: List[Challenge])

case class RespondChallengeRequest(
  user: User,
  opponentSolutionId: String,
  accept: Boolean)
case class RespondChallengeResult(
  allowed: ProfileModificationResult,
  profile: Option[Profile] = None)


private[domain] trait ChallengesAPI { this: DomainAPIComponent#DomainAPI with DBAccessor =>

  /**
   * Challenge someone to jon a battle.
   */ // TODO: Move it out of user's API
  def makeChallenge(request: MakeChallengeRequest): ApiResult[MakeChallengeResult] = handleDbException {
    import request._

    def makeChallenge(mySolution: Solution, opponentSolution: Solution): ApiResult[MakeChallengeResult] = {
      db.user.addBattleRequest(
        opponentSolution.info.authorId,
        Challenge(
          opponentId = user.id,
          mySolutionId = Some(opponentSolution.id),
          opponentSolutionId = Some(mySolution.id),
          status = ChallengeStatus.Requests)) ifSome { opponent =>

        runWhileSome(user) ( { user =>
          db.user.addBattleRequest(
            user.id,
            Challenge(
              opponentId = opponent.id,
              mySolutionId = Some(mySolution.id),
              opponentSolutionId = Some(opponentSolution.id),
              status = ChallengeStatus.Requested))
        }, { user =>
          // TODO: substract assets for invitation.
          // TODO: test it calls db correctly.
          Some(user)
        }
        ) ifSome { user =>
          {
            makeTask(MakeTaskRequest(user, Some(TaskType.ChallengeBattle)))
          } map { r =>
            OkApiResult(MakeChallengeResult(OK, Some(r.user.profile)))
          }
        }
      }
    }

    db.solution.readById(mySolutionId).fold[ApiResult[MakeChallengeResult]](OkApiResult(MakeChallengeResult(OutOfContent))) { mySolution =>
      db.solution.readById(opponentSolutionId).fold[ApiResult[MakeChallengeResult]](OkApiResult(MakeChallengeResult(OutOfContent))) { opponentSolution =>
        user.canChallengeBattle(mySolution, opponentSolution) match {
          case OK =>
            makeChallenge(mySolution, opponentSolution)
          case reason =>
            OkApiResult(MakeChallengeResult(reason))
        }
      }
    }
  }

  /**
   * Get all battle requests we have.
   */
  def getMyChallenges(request: GetMyChallengesRequest): ApiResult[GetBattleRequestsResult] = handleDbException {
    OkApiResult(GetBattleRequestsResult(
      allowed = OK,
      requests = request.user.battleRequests))
  }

  /**
   * Respond on battle request.
   */
  def respondChallenge(request: RespondChallengeRequest): ApiResult[RespondChallengeResult] = handleDbException {
    import request._

    def createBattleForRequest(br: Challenge): ApiResult[RespondChallengeResult] = {
      val newStatus = if (accept) ChallengeStatus.Accepted else ChallengeStatus.Rejected

      db.user.updateBattleRequest(user.id, br.mySolutionId, br.opponentSolutionId, newStatus.toString) ifSome { user =>

        db.user.updateBattleRequest(
          br.opponentId, br.opponentSolutionId, br.mySolutionId, newStatus.toString) ifSome { opponent =>
          if (accept) {
            db.solution.readById(br.mySolutionId).fold[ApiResult[RespondChallengeResult]](
              OkApiResult(RespondChallengeResult(OutOfContent))) { mySolution =>
              db.solution.readById(br.opponentSolutionId).fold[ApiResult[RespondChallengeResult]](
                OkApiResult(RespondChallengeResult(OutOfContent))) { opponentSolution =>
                createBattle(CreateBattleRequest(List(mySolution, opponentSolution))) map {
                  sendMessage(SendMessageRequest(opponent, MessageBattleRequestAccepted(
                    challengeId = br.mySolutionId)))
                } map {
                  OkApiResult(RespondChallengeResult(OK, Some(user.profile)))
                }
              }
            }
          } else {
            // TODO: return money back.
            // TODO: test me.
            sendMessage(SendMessageRequest(opponent, MessageBattleRequestRejected(br.mySolutionId))) map {
              OkApiResult(RespondChallengeResult(OK, Some(user.profile)))
            }
          }
        }
      }
    }

    user.battleRequests.find(
      br =>
        (br.status == ChallengeStatus.Requests) &&
          (br.opponentSolutionId == opponentSolutionId)
    ).fold[ApiResult[RespondChallengeResult]] {
      Logger.trace(s"Unable to find battle request with status Requests and opponentSolutionId equal to $opponentSolutionId")
      OkApiResult(RespondChallengeResult(OutOfContent))
    } { br =>
      createBattleForRequest(br)
    }
  }
}

