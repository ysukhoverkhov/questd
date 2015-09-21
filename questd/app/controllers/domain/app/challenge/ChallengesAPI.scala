package controllers.domain.app.challenge

import components._
import controllers.domain._
import controllers.domain.app.protocol.ProfileModificationResult._
import controllers.domain.helpers._
import models.domain.challenge.{Challenge, ChallengeStatus}
import models.domain.user.User
import models.domain.user.profile.Profile
import models.view.SolutionView

case class MakeChallengeRequest(
  user: User,
  opponentId: String,
  myQuestId: Option[String],
  mySolutionId: Option[String])
case class MakeChallengeResult(
  allowed: ProfileModificationResult,
  profile: Option[Profile] = None,
  modifiedSolutions: List[SolutionView] = List.empty)

case class GetChallengeRequest(
  user: User,
  challengeId: String)
case class GetChallengeResult(
  allowed: ProfileModificationResult,
  challenge: Option[Challenge] = None)

case class GetMyChallengesRequest(
  user: User,
  statuses: List[ChallengeStatus.Value],
  pageNumber: Int,
  pageSize: Int)
case class GetMyChallengesResult(
  allowed: ProfileModificationResult,
  challenges: List[Challenge] = List.empty)

case class GetChallengesToMeRequest(
  user: User,
  statuses: List[ChallengeStatus.Value],
  pageNumber: Int,
  pageSize: Int)
case class GetChallengesToMeResult(
  allowed: ProfileModificationResult,
  challenges: List[Challenge] = List.empty)

case class RespondChallengeRequest(
  user: User,
  challengeId: String,
  accepted: Boolean,
  solutionId: Option[String] = None)
case class RespondChallengeResult(
  allowed: ProfileModificationResult,
  profile: Option[Profile] = None,
  modifiedSolutions: List[SolutionView] = List.empty)


private[domain] trait ChallengesAPI { this: DomainAPIComponent#DomainAPI with DBAccessor =>

  /**
   * Challenge someone to jon a battle.
   */
  def makeChallenge(request: MakeChallengeRequest): ApiResult[MakeChallengeResult] = handleDbException {

//    def makeChallenge(mySolution: Solution, opponentSolution: Solution): ApiResult[MakeChallengeResult] = {
//      db.user.addBattleRequest(
//        opponentSolution.info.authorId,
//        Challenge(
//          opponentId = user.id,
//          mySolutionId = Some(opponentSolution.id),
//          opponentSolutionId = Some(mySolution.id),
//          status = ChallengeStatus.Requests)) ifSome { opponent =>
//
//        runWhileSome(user) ( { user =>
//          db.user.addBattleRequest(
//            user.id,
//            Challenge(
//              opponentId = opponent.id,
//              mySolutionId = Some(mySolution.id),
//              opponentSolutionId = Some(opponentSolution.id),
//              status = ChallengeStatus.Requested))
//        }, { user =>
//          // TODO: substract assets for invitation.
//          // TODO: test it calls db correctly.
//          Some(user)
//        }
//        ) ifSome { user =>
//          {
//            makeTask(MakeTaskRequest(user, Some(TaskType.ChallengeBattle)))
//          } map { r =>
//            OkApiResult(MakeChallengeResult(OK, Some(r.user.profile), Some(SolutionView(opponentSolution, user))))
//          }
//        }
//      }
//    }


//    if (!(mySolutionId.nonEmpty ^^ myQuestId.nonEmpty)) {
//      OkApiResult(MakeChallengeResult(allowed = InvalidState))
//    } else {
//      val contentExists =
//        myQuestId.fold(true){ myQuestId =>
//          db.quest.readById(myQuestId).nonEmpty
//        } && mySolutionId.fold(true) { mySolutionId =>
//          db.solution.readById(mySolutionId).nonEmpty
//        }
//      if(!contentExists) {
//        OkApiResult(MakeChallengeResult(OutOfContent))
//      } else {
//        user.canChallengeBattle(mySolution, opponentSolution) match {
//          case OK =>
//            makeChallenge(mySolution, opponentSolution)
//          case reason =>
//            OkApiResult(MakeChallengeResult(reason))
//        }
//      }
//    }


            OkApiResult(MakeChallengeResult(OK))
  }

  /**
   * Returns challenge by id if we are its participant.
   */
  def getChallenge(request: GetChallengeRequest): ApiResult[GetChallengeResult] = handleDbException {
    db.challenge.readById(request.challengeId).fold {
      OkApiResult(GetChallengeResult(OutOfContent))
    } { c =>
      if (List(c.myId, c.opponentId).contains(request.user.id)) {
        OkApiResult(GetChallengeResult(OK, Some(c)))
      } else {
        OkApiResult(GetChallengeResult(OutOfContent))
      }
    }
  }

  /**
   * Get all battle requests we've made.
   */
  def getMyChallenges(request: GetMyChallengesRequest): ApiResult[GetMyChallengesResult] = handleDbException {
    val pageSize = adjustedPageSize(request.pageSize)
    val pageNumber = adjustedPageNumber(request.pageNumber)

    OkApiResult(GetMyChallengesResult(
      allowed = OK,
      db.challenge.allWithParams(
        myId = Some(request.user.id),
        statuses = request.statuses,
        skip = pageSize * pageNumber)
        .take(pageSize)
        .toList))
  }

  /**
   * Get challenges made to us.
   */
  def getChallengesToMe(request: GetChallengesToMeRequest): ApiResult[GetChallengesToMeResult] = handleDbException {
    val pageSize = adjustedPageSize(request.pageSize)
    val pageNumber = adjustedPageNumber(request.pageNumber)

    OkApiResult(GetChallengesToMeResult(
      allowed = OK,
      db.challenge.allWithParams(
        opponentId = Some(request.user.id),
        statuses = request.statuses,
        skip = pageSize * pageNumber)
        .take(pageSize)
        .toList))
  }

  /**
   * Respond on battle request.
   */
  def respondChallenge(request: RespondChallengeRequest): ApiResult[RespondChallengeResult] = handleDbException {
//    import request._
//
//    def createBattleForRequest(br: Challenge): ApiResult[RespondChallengeResult] = {
//      val newStatus = if (accept) ChallengeStatus.Accepted else ChallengeStatus.Rejected
//
//      db.user.updateBattleRequest(user.id, br.mySolutionId, br.opponentSolutionId, newStatus.toString) ifSome { user =>
//
//        db.user.updateBattleRequest(
//          br.opponentId, br.opponentSolutionId, br.mySolutionId, newStatus.toString) ifSome { opponent =>
//          if (accept) {
//            db.solution.readById(br.mySolutionId).fold[ApiResult[RespondChallengeResult]](
//              OkApiResult(RespondChallengeResult(OutOfContent))) { mySolution =>
//              db.solution.readById(br.opponentSolutionId).fold[ApiResult[RespondChallengeResult]](
//                OkApiResult(RespondChallengeResult(OutOfContent))) { opponentSolution =>
//                createBattle(CreateBattleRequest(List(mySolution, opponentSolution))) map {
//                  sendMessage(SendMessageRequest(opponent, MessageBattleRequestAccepted(challengeId = br.mySolutionId))) // TODO fix it.
//                } map {
//                  OkApiResult(RespondChallengeResult(OK, Some(user.profile), Some(SolutionView(opponentSolution, user))))
//                }
//              }
//            }
//          } else {
//            // TODO: return money back.
//            // TODO: test me.
//            sendMessage(SendMessageRequest(opponent, MessageBattleRequestRejected(br.mySolutionId))) map {
//              OkApiResult(RespondChallengeResult(
//                OK,
//                Some(user.profile),
//                None))
//            }
//          }
//        }
//      }
//    }
//
//    user.battleRequests.find(
//      br =>
//        (br.status == ChallengeStatus.Requests) &&
//          (br.opponentSolutionId == opponentSolutionId)
//    ).fold[ApiResult[RespondChallengeResult]] {
//      Logger.trace(s"Unable to find battle request with status Requests and opponentSolutionId equal to $opponentSolutionId")
//      OkApiResult(RespondChallengeResult(OutOfContent))
//    } { br =>
//      createBattleForRequest(br)
//    }
                  OkApiResult(RespondChallengeResult(                    OK))
  }
}

