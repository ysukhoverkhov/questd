package controllers.domain.app.user

import components._
import controllers.domain._
import controllers.domain.app.battle.VoteBattleRequest
import controllers.domain.app.protocol.CommonCode
import controllers.domain.helpers._
import models.domain.battle.BattleStatus
import models.domain.user._
import models.domain.user.friends.FriendshipStatus
import models.domain.user.profile.{Profile, TaskType}
import models.view.BattleView

object VoteBattleByUserCode extends Enumeration with CommonCode {
  val InvalidBattleState = Value
  val BattleNotFound = Value
  val BattleSideNotFound = Value
  val BattleAlreadyVoted = Value
  val ParticipantsCantVote = Value
}
case class VoteBattleByUserRequest(user: User, battleId: String, solutionId: String)
case class VoteBattleByUserResult(
  allowed: VoteBattleByUserCode.Value,
  profile: Option[Profile] = None,
  modifiedBattles: List[BattleView] = List.empty)

private[domain] trait VoteBattleAPI {
  this: DomainAPIComponent#DomainAPI with DBAccessor =>

  /**
   * Vote for a Battle.
   */
  def voteBattleByUser(request: VoteBattleByUserRequest): ApiResult[VoteBattleByUserResult] = handleDbException {
    import VoteBattleByUserCode._
    import request._

    user.canVoteBattle(battleId) match {
      case OK =>

        db.battle.readById(battleId) match {
          case Some(b) =>
          if (b.info.status == BattleStatus.Resolved) {
            OkApiResult(VoteBattleByUserResult(InvalidBattleState))
          } else {

            b.info.battleSides.find(_.solutionId == solutionId) match {
              case Some(battleSide) =>
                val authorId = battleSide.authorId
                val isFriend = user.friends.filter(_.status == FriendshipStatus.Accepted).map(_.friendId).contains(authorId)

                runWhileSome(user)(
                { u =>
                  db.user.recordBattleVote(u.id, battleId, solutionId)
                }
                ) ifSome { u =>
                  {
                    voteBattle(VoteBattleRequest(b, solutionId, isFriend))
                  } map { r =>
                    makeTask(MakeTaskRequest(u, taskType = Some(TaskType.VoteBattle)))
                  } map { r =>
                    OkApiResult(VoteBattleByUserResult(
                      allowed = OK,
                      profile = Some(r.user.profile),
                      modifiedBattles = List(BattleView(b, r.user))))
                  }
                }

              case None =>
                // Battle side with given solution is not found.
                OkApiResult(VoteBattleByUserResult(BattleSideNotFound))
            }
          }
          case None =>
            // Battle with given id is not found.
            OkApiResult(VoteBattleByUserResult(BattleNotFound))
        }

      case result => OkApiResult(VoteBattleByUserResult(result))
    }
  }
}

