package controllers.domain.app.challenge

import components._
import controllers.domain._
import controllers.domain.app.protocol.CommonCode
import controllers.domain.app.user.{CreateBattleRequest, MakeTaskRequest, SendMessageRequest}
import controllers.domain.helpers._
import models.domain.challenge.{Challenge, ChallengeStatus}
import models.domain.solution.Solution
import models.domain.user.User
import models.domain.user.message.{MessageChallengeAccepted, MessageChallengeRejected}
import models.domain.user.profile.{Profile, TaskType}
import models.view.{QuestView, SolutionView}
import play.Logger


object MakeQuestChallengeCode extends Enumeration with CommonCode {
  val QuestNotFound = Value
  val OpponentAlreadyChallenged = Value
  val QuestNotInRotation = Value
  val OpponentNotAFriend = Value
}
case class MakeQuestChallengeRequest(
  user: User,
  opponentId: String,
  myQuestId: String)
case class MakeQuestChallengeResult(
  allowed: MakeQuestChallengeCode.Value,
  profile: Option[Profile] = None,
  challenge: Option[Challenge] = None,
  modifiedQuests: List[QuestView] = List.empty)


object MakeSolutionChallengeCode extends Enumeration with CommonCode {
  val SolutionNotFound = Value
  val OpponentNotFound = Value
  val OpponentAlreadyChallenged = Value
  val SolutionNotInRotation = Value
  val OpponentNotAFriendAndDoesNotHaveSolution = Value
}
case class MakeSolutionChallengeRequest(
  user: User,
  opponentId: String,
  mySolutionId: String)
case class MakeSolutionChallengeResult(
  allowed: MakeSolutionChallengeCode.Value,
  profile: Option[Profile] = None,
  challenge: Option[Challenge] = None,
  modifiedSolutions: List[SolutionView] = List.empty)


object GetChallengeCode extends Enumeration with CommonCode {
  val ChallengeNotFound = Value
  val UserNotParticipant = Value
}
case class GetChallengeRequest(
  user: User,
  challengeId: String)
case class GetChallengeResult(
  allowed: GetChallengeCode.Value,
  challenge: Option[Challenge] = None)


object GetMyChallengesCode extends Enumeration with CommonCode
case class GetMyChallengesRequest(
  user: User,
  statuses: List[ChallengeStatus.Value],
  pageNumber: Int,
  pageSize: Int)
case class GetMyChallengesResult(
  allowed: GetMyChallengesCode.Value,
  challenges: List[Challenge] = List.empty)


object GetChallengesToMeCode extends Enumeration with CommonCode
case class GetChallengesToMeRequest(
  user: User,
  statuses: List[ChallengeStatus.Value],
  pageNumber: Int,
  pageSize: Int)
case class GetChallengesToMeResult(
  allowed: GetChallengesToMeCode.Value,
  challenges: List[Challenge] = List.empty)


object AcceptChallengeCode extends Enumeration with CommonCode {
  val ChallengeNotFound = Value
  val SolutionNotFound = Value
  val SolutionNotForTheQuest = Value
  val CannotAcceptOwnChallenge = Value
  val WrongChallengeState = Value
}
case class AcceptChallengeRequest(
  user: User,
  challengeId: String,
  solutionId: String)
case class AcceptChallengeResult(
  allowed: AcceptChallengeCode.Value,
  profile: Option[Profile] = None,
  modifiedSolutions: List[SolutionView] = List.empty)


object RejectChallengeCode extends Enumeration with CommonCode {
  val ChallengeNotFound = Value
  val CannotAcceptOwnChallenge = Value
  val WrongChallengeState = Value
}
case class RejectChallengeRequest(
  user: User,
  challengeId: String)
case class RejectChallengeResult(
  allowed: RejectChallengeCode.Value,
  profile: Option[Profile] = None)


case class GetAllChallengesForCrawlerRequest()
case class GetAllChallengesForCrawlerResult(challenges: Iterator[Challenge])


case class AutoRejectChallengeRequest(challenge: Challenge)
case class AutoRejectChallengeResult()



private[domain] trait ChallengesAPI { this: DomainAPIComponent#DomainAPI with DBAccessor =>

  /**
   * Challenge someone to solve my quest.
   */
  def makeQuestChallenge(request: MakeQuestChallengeRequest): ApiResult[MakeQuestChallengeResult] = handleDbException {
    import MakeQuestChallengeCode._
    import request._

    db.quest.readById(myQuestId).fold[ApiResult[MakeQuestChallengeResult]] {
      OkApiResult(MakeQuestChallengeResult(QuestNotFound))
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
            OkApiResult(MakeQuestChallengeResult(
              allowed = OK,
              profile = Some(r.user.profile),
              challenge = Some(challenge),
              modifiedQuests = List(QuestView(myQuest, user))))
          }

        case OpponentAlreadyChallenged => // TODO: test this case
          val challenge = db.challenge.findByParticipantsAndQuest(
            (user.id, opponentId), myQuestId)
            .foldLeft[Option[Challenge]](None){(r, v) => Some(v)}
          OkApiResult(MakeQuestChallengeResult(
            allowed = OK,
            challenge = challenge))

        case reason =>
          OkApiResult(MakeQuestChallengeResult(reason))
      }
    }
  }


  /**
   * Challenge someone to solve quest I've solved.
   */
  def makeSolutionChallenge(request: MakeSolutionChallengeRequest): ApiResult[MakeSolutionChallengeResult] = handleDbException {
    import MakeSolutionChallengeCode._
    import request._

    db.solution.readById(mySolutionId).fold[ApiResult[MakeSolutionChallengeResult]] {
      OkApiResult(MakeSolutionChallengeResult(SolutionNotFound))
    } { mySolution =>
      db.user.readById(opponentId).fold[ApiResult[MakeSolutionChallengeResult]] {
        OkApiResult(MakeSolutionChallengeResult(OpponentNotFound))
      } { opponent =>
        user.canChallengeWithSolution(opponent = opponent, mySolution = mySolution) match {
          case OK =>
            val challenge = Challenge(
              myId = user.id,
              opponentId = opponentId,
              questId = mySolution.info.questId,
              mySolutionId = Some(mySolutionId),
              status = ChallengeStatus.Requested)

            db.challenge.create(challenge)

            // substract assets for invitation.

            {
              makeTask(MakeTaskRequest(user, Some(TaskType.ChallengeBattle)))
            } map { r =>
              OkApiResult(MakeSolutionChallengeResult(
                allowed = OK,
                profile = Some(r.user.profile),
                challenge = Some(challenge),
                modifiedSolutions = List(SolutionView(mySolution, user))))
            }

          case OpponentAlreadyChallenged => // TODO: test this case
            val challenge = db.challenge.findByParticipantsAndQuest(
              (user.id, opponentId), mySolution.info.questId)
              .foldLeft[Option[Challenge]](None){(r, v) => Some(v)}
            OkApiResult(MakeSolutionChallengeResult(
              allowed = OK,
              challenge = challenge))

          case reason =>
            OkApiResult(MakeSolutionChallengeResult(reason))
        }
      }
    }
  }

  /**
   * Returns challenge by id if we are its participant.
   */
  def getChallenge(request: GetChallengeRequest): ApiResult[GetChallengeResult] = handleDbException {
    import GetChallengeCode._

    db.challenge.readById(request.challengeId).fold {
      OkApiResult(GetChallengeResult(ChallengeNotFound))
    } { c =>
      if (List(c.myId, c.opponentId).contains(request.user.id)) {
        OkApiResult(GetChallengeResult(OK, Some(c)))
      } else {
        OkApiResult(GetChallengeResult(UserNotParticipant))
      }
    }
  }

  /**
   * Get all battle requests we've made.
   */
  def getMyChallenges(request: GetMyChallengesRequest): ApiResult[GetMyChallengesResult] = handleDbException {
    import GetMyChallengesCode._

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
    import GetChallengesToMeCode._

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
  def acceptChallenge(request: AcceptChallengeRequest): ApiResult[AcceptChallengeResult] = handleDbException {
    import AcceptChallengeCode._
    import request._

    db.challenge.readById(challengeId).fold[ApiResult[AcceptChallengeResult]] {
      OkApiResult(AcceptChallengeResult(ChallengeNotFound))
    } { challenge: Challenge =>
      db.solution.readById(solutionId).fold[ApiResult[AcceptChallengeResult]] {
        OkApiResult(AcceptChallengeResult(SolutionNotFound))
      } { solution: Solution =>
        user.canAcceptChallengeWithSolution(challenge, solution) match {
          case OK =>
            db.challenge.updateChallenge(challenge.id, ChallengeStatus.Accepted, Some(solution.id)) ifSome { updatedChallenge =>
              db.user.readById(challenge.myId) ifSome { challenger =>
                sendMessage(
                  SendMessageRequest(
                    challenger, MessageChallengeAccepted(challengeId = challenge.id)))
              }

              updatedChallenge.mySolutionId.fold[ApiResult[AcceptChallengeResult]] {
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
  def rejectChallenge(request: RejectChallengeRequest): ApiResult[RejectChallengeResult] = handleDbException {
    import RejectChallengeCode._
    import request._

    db.challenge.readById(challengeId).fold[ApiResult[RejectChallengeResult]] {
      OkApiResult(RejectChallengeResult(ChallengeNotFound))
    } { challenge: Challenge =>
      user.canRejectChallenge(challenge) match {
        case OK =>
          db.challenge.updateChallenge(challenge.id, ChallengeStatus.Rejected, None) ifSome { updatedChallenge =>
            db.user.readById(challenge.myId) ifSome { challenger =>
              sendMessage(
                SendMessageRequest(
                  challenger, MessageChallengeRejected(challengeId = challenge.id)))

            // return money back.
            }

            OkApiResult(RejectChallengeResult(OK, Some(request.user.profile)))
          }

        case result =>
          OkApiResult(RejectChallengeResult(result))
      }
    }
  }

  /**
   * Automatically reject challenge
   */
  def autoRejectChallenge(request: AutoRejectChallengeRequest): ApiResult[AutoRejectChallengeResult] = handleDbException {
    import request.challenge

    db.user.readById(challenge.opponentId).fold[ApiResult[AutoRejectChallengeResult]] {
      Logger.error(s"No opponent for challenge in db. challengeId ${challenge.id}")
      OkApiResult(AutoRejectChallengeResult())
    } { opponent =>
      rejectChallenge(RejectChallengeRequest(opponent, challenge.id)) map OkApiResult(AutoRejectChallengeResult())
    }
  }

  /**
   * Returns all challenges what should be crawled.
   */
  def getAllChallengesForCrawler(request: GetAllChallengesForCrawlerRequest): ApiResult[GetAllChallengesForCrawlerResult] = handleDbException {
    OkApiResult(GetAllChallengesForCrawlerResult(db.challenge.allWithParams(
      statuses = List(ChallengeStatus.Requested))))
  }
}

