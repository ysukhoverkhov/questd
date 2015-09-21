package controllers.domain.app.challenge

import components._
import controllers.domain._
import controllers.domain.app.protocol.ProfileModificationResult._
import controllers.domain.app.user.{SendMessageRequest, CreateBattleResult, CreateBattleRequest, MakeTaskRequest}
import controllers.domain.helpers._
import models.domain.challenge.{Challenge, ChallengeStatus}
import models.domain.solution.Solution
import models.domain.user.User
import models.domain.user.message.MessageChallengeAccepted
import models.domain.user.profile.{Profile, TaskType}
import models.view.{QuestView, SolutionView}

case class MakeQuestChallengeRequest(
  user: User,
  opponentId: String,
  myQuestId: String)
case class MakeQuestChallengeResult(
  allowed: ProfileModificationResult,
  profile: Option[Profile] = None,
  modifiedQuests: List[QuestView] = List.empty)

case class MakeSolutionChallengeRequest(
  user: User,
  opponentId: String,
  mySolutionId: String)
case class MakeSolutionChallengeResult(
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

case class AcceptChallengeRequest(
  user: User,
  challengeId: String,
  solutionId: String)
case class AcceptChallengeResult(
  allowed: ProfileModificationResult,
  profile: Option[Profile] = None,
  modifiedSolutions: List[SolutionView] = List.empty)

case class RejectChallengeRequest(
  user: User,
  challengeId: String)
case class RejectChallengeResult(
  allowed: ProfileModificationResult,
  profile: Option[Profile] = None)

private[domain] trait ChallengesAPI { this: DomainAPIComponent#DomainAPI with DBAccessor =>

  /**
   * Challenge someone to solve my quest.
   */
  def makeQuestChallenge(request: MakeQuestChallengeRequest): ApiResult[MakeQuestChallengeResult] = handleDbException {
    import request._

    db.quest.readById(myQuestId).fold[ApiResult[MakeQuestChallengeResult]] {
      OkApiResult(MakeQuestChallengeResult(OutOfContent))
    } { myQuest =>
      user.canChallengeWithQuest(opponentId = opponentId, myQuest = myQuest) match {
        case OK =>
          val challenge = Challenge(
            myId = user.id,
            opponentId = opponentId,
            questId = myQuestId,
            status = ChallengeStatus.Requested)

          db.challenge.create(challenge)

          {
            makeTask(MakeTaskRequest(user, Some(TaskType.ChallengeBattle)))
          } map { r =>
            OkApiResult(MakeQuestChallengeResult(OK, Some(r.user.profile), List(QuestView(myQuest, user))))
          }

        case reason =>
          OkApiResult(MakeQuestChallengeResult(reason))
      }
    }
  }


  /**
   * Challenge someone to solve quest I've solved.
   */
  def makeSolutionChallenge(request: MakeSolutionChallengeRequest): ApiResult[MakeSolutionChallengeResult] = handleDbException {
    import request._

    db.solution.readById(mySolutionId).fold[ApiResult[MakeSolutionChallengeResult]] {
      OkApiResult(MakeSolutionChallengeResult(OutOfContent))
    } { mySolution =>
      user.canChallengeWithSolution(opponentId = opponentId, mySolution = mySolution) match {
        case OK =>
          val challenge = Challenge(
            myId = user.id,
            opponentId = opponentId,
            questId = mySolution.info.questId,
            mySolutionId = Some(mySolutionId),
            status = ChallengeStatus.Requested)

          db.challenge.create(challenge)

        // TODO: substract assets for invitation.
        // TODO: test it calls db correctly.

          {
            makeTask(MakeTaskRequest(user, Some(TaskType.ChallengeBattle)))
          } map { r =>
            OkApiResult(MakeSolutionChallengeResult(OK, Some(r.user.profile), List(SolutionView(mySolution, user))))
          }

        case reason =>
          OkApiResult(MakeSolutionChallengeResult(reason))
      }
    }
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
   */ // Test each and every branch.
  def acceptChallenge(request: AcceptChallengeRequest): ApiResult[AcceptChallengeResult] = handleDbException {
    import request._

    db.challenge.readById(challengeId).fold[ApiResult[AcceptChallengeResult]] {
      OkApiResult(AcceptChallengeResult(OutOfContent))
    } { challenge: Challenge =>
        db.solution.readById(solutionId).fold[ApiResult[AcceptChallengeResult]] {
          OkApiResult(AcceptChallengeResult(OutOfContent))
        } { solution: Solution =>
          user.canAcceptChallengeWithSolution(challenge, solution) match {
            case OK =>
              db.challenge.updateChallenge(challenge.id, ChallengeStatus.Accepted, Some(solution.id)) ifSome { updatedChallenge =>
                db.user.readById(challenge.myId) ifSome { challenger =>
                  sendMessage(
                    SendMessageRequest(
                      challenger, MessageChallengeAccepted(challengeId = challenge.id)))
                }

                updatedChallenge.mySolutionId.fold {
                  OkApiResult(
                    AcceptChallengeResult(
                      allowed = OK,
                      profile = Some(user.profile),
                      modifiedSolutions = List(SolutionView(solution, user))
                    ))
                } { mySolutionId =>
                  db.solution.readById(mySolutionId) ifSome { mySolution =>
                    createBattle(CreateBattleRequest(List(mySolution, solution))) map {
                      db.user.readById(user.id) ifSome { updatedUser =>
                        OkApiResult(
                          AcceptChallengeResult(
                            allowed = OK,
                            profile = Some(updatedUser.profile),
                            modifiedSolutions = List(
                              SolutionView(mySolution, updatedUser), SolutionView(solution, updatedUser))
                          ))
                      }
                    }
                  }
                }
              }

            case result =>
              OkApiResult(AcceptChallengeResult(result))
          }
        }
      }
    }


    /**
     * Respond on battle request.
     */
    def rejectChallenge(request: AcceptChallengeRequest): ApiResult[AcceptChallengeResult] = handleDbException {
      import request._

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
      db.challenge.readById(challengeId).fold {
        OkApiResult(AcceptChallengeResult(OutOfContent))
      } { challenge =>
        user.canRespondChallenge(challenge) match {
          case OK =>
            if (accepted) {
              solutionId.fold {
                OkApiResult(AcceptChallengeResult(OutOfContent))
              } {
              }



              db.solution.readById(solutionId).fold {
                OkApiResult(AcceptChallengeResult(OutOfContent))
              } { solution =>
              }
              user.canAcceptChallengeWithSolution(challenge) match {
              } else {

              }

              OkApiResult(AcceptChallengeResult(OK))
              case result =>
                OkApiResult(AcceptChallengeResult(result))
            }
        }
        user.battleRequests.find(
      br =>
        (br.status == ChallengeStatus.Requests) &&
          (br.opponentSolutionId == opponentSolutionId)
    ).fold[ApiResult[AcceptChallengeResult]] {
      Logger.trace(s"Unable to find battle request with status Requests and opponentSolutionId equal to $opponentSolutionId")
      OkApiResult(AcceptChallengeResult(OutOfContent))
    } { br =>
      createBattleForRequest(br)
    }

      }

}

