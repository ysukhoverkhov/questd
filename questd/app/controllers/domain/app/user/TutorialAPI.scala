package controllers.domain.app.user

import components._
import controllers.domain.app.protocol.ProfileModificationResult._
import controllers.domain.helpers._
import controllers.domain.{DomainAPIComponent, _}
import models.domain.common.Assets
import models.domain.tutorial.{TutorialElement, TutorialPlatform}
import models.domain.user._
import models.domain.user.profile.{DailyTasks, Profile, Task, TutorialState}
import models.domain.user.timeline.{TimeLineReason, TimeLineType}

case class GetCommonTutorialRequest(platform: TutorialPlatform.Value)
case class GetCommonTutorialResult(tutorialElements: List[TutorialElement])

case class GetTutorialRequest(user: User, platform: TutorialPlatform.Value)
case class GetTutorialResult(tutorialElements: List[TutorialElement])

case class CloseTutorialElementRequest(user: User, platform: TutorialPlatform.Value, elementId: String)
case class CloseTutorialElementResult(allowed: ProfileModificationResult, profile: Option[Profile] = None)

case class AssignTutorialTaskRequest(user: User, platform: TutorialPlatform.Value, taskId: String)
case class AssignTutorialTaskResult(allowed: ProfileModificationResult, profile: Option[Profile] = None)

case class IncTutorialTaskRequest(user: User, taskId: String)
case class IncTutorialTaskResult(allowed: ProfileModificationResult, profile: Option[Profile] = None)

case class AssignTutorialQuestRequest(user: User, platform: TutorialPlatform.Value, questId: String)
case class AssignTutorialQuestResult(allowed: ProfileModificationResult, profile: Option[Profile] = None)

case class CreateTutorialBattlesRequest(user: User, platform: TutorialPlatform.Value)
case class CreateTutorialBattlesResult(allowed: ProfileModificationResult, profile: Option[Profile] = None)

case class ResetTutorialRequest(user: User)
case class ResetTutorialResult(allowed: ProfileModificationResult, profile: Option[Profile] = None)

private[domain] trait TutorialAPI { this: DomainAPIComponent#DomainAPI with DBAccessor =>

  /**
   * Get common for all users tutorial for the platform.
   */
  def getCommonTutorial(request: GetCommonTutorialRequest): ApiResult[GetCommonTutorialResult] = handleDbException {
    val commonScenario: List[TutorialElement] =
      db.tutorial.readById(request.platform.toString) map {_.elements} getOrElse List.empty

    OkApiResult(GetCommonTutorialResult(commonScenario))
  }

  /**
   * Get actual for current user tutorial.
   */
  def getTutorial(request: GetTutorialRequest): ApiResult[GetTutorialResult] = handleDbException {
    val commonScenario: List[TutorialElement] =
      db.tutorial.readById(request.platform.toString) map {_.elements} getOrElse List.empty

    OkApiResult(GetTutorialResult(commonScenario))
  }

  /**
   * Set state of tutorial for a specified platform.
   */
  def closeTutorialElement(request: CloseTutorialElementRequest): ApiResult[CloseTutorialElementResult] = handleDbException {
    import request._

    db.user.addClosedTutorialElement(user.id, platform.toString, elementId) ifSome { v =>
      OkApiResult(CloseTutorialElementResult(OK, Some(v.profile)))
    }
  }

  /**
   * Assign client task to user.
   */
  def assignTutorialTask(request: AssignTutorialTaskRequest): ApiResult[AssignTutorialTaskResult] = handleDbException {
    import request._
    // 1. check is the task was already given.
    if (user.profile.tutorialStates(platform.toString).usedTutorialTaskIds.contains(taskId)) {
      OkApiResult(AssignTutorialTaskResult(LimitExceeded))
    } else {
      db.tutorialTask.readById(taskId) match {
        case Some(t) =>
          // 2. If rewardReceived is true remove all tasks and give current one as a solely task.
          if (user.profile.dailyTasks.rewardReceived) {
            db.user.resetTasks(
              user.id,
              DailyTasks(tasks = List.empty, reward = Assets()),
              user.getResetTasksTimeout)
          }

          {
            // 3. Add task to list of assigned tutorial tasks.
            db.user.addTutorialTaskAssigned(id = user.id, platform = platform.toString, taskId = taskId)
          } ifSome { v =>
            // 4. Add reward of current task to reward for current daily tasks and increase timeout to infinity.
            val taskToAdd = t.task

            val ratingReward = taskToAdd.reward.rating match {
              case r if r != 0 =>
                Some(Assets(rating = r))
              case _ => None
            }

            db.user.addTasks(
              user.id,
              List(taskToAdd.copy(reward = Assets(coins = taskToAdd.reward.coins, money = taskToAdd.reward.money))),
              ratingReward) ifSome { u =>
              {
                updateDailyTasksCompletedFraction(UpdateDailyTasksCompletedFractionRequest(u.user))
              } map { r =>
                OkApiResult(AssignTutorialTaskResult(OK, Some(r.user.profile)))
              }
            }
          }
        case None =>
          OkApiResult(AssignTutorialTaskResult(OutOfContent))
      }
    }
  }

  /**
   * Inc by one progress of tutorial task.
   */
  def incTutorialTask(request: IncTutorialTaskRequest): ApiResult[IncTutorialTaskResult] = handleDbException {
    import request._

    user.profile.dailyTasks.tasks.find(t => t.tutorialTaskId.contains(taskId)).fold[ApiResult[IncTutorialTaskResult]] {
      OkApiResult(IncTutorialTaskResult(OutOfContent))
    }
    { t: Task =>
      {
        makeTask(MakeTaskRequest(user = user, taskId = Some(t.id)))
      } map { r =>
        OkApiResult(IncTutorialTaskResult(OK, Some(r.user.profile)))
      }
    }
  }

  /**
   * Assigns new tutorial quest by client's request.
   */
  def assignTutorialQuest(request: AssignTutorialQuestRequest): ApiResult[AssignTutorialQuestResult] = handleDbException {
    import request._

    if (user.profile.tutorialStates(platform.toString).usedTutorialQuestIds.contains(questId)) {
      OkApiResult(AssignTutorialQuestResult(LimitExceeded))
    } else if (api.configNamed("Tutorial")(api.TutorialConfigParams.TutorialQuestId) != questId) {
      OkApiResult(AssignTutorialQuestResult(OutOfContent))
    } else {
      db.quest.readById(questId) match {
        case Some(q) =>
          db.user.addTutorialQuestAssigned(
            user.id,
            platform.toString,
            questId) ifSome { user =>

            addToTimeLine(
              AddToTimeLineRequest(
                user = user,
                reason = TimeLineReason.Has,
                objectType = TimeLineType.Quest,
                objectId = questId,
                actorId = Some(q.info.authorId))) map { r =>
              OkApiResult(AssignTutorialQuestResult(OK, Some(r.user.profile)))
            }
          }
        case None =>
          OkApiResult(AssignTutorialQuestResult(OutOfContent))
      }
    }
  }

  /**
   * Creates tutorial battles for all solutions without battles.
   */
  def createTutorialBattles(request: CreateTutorialBattlesRequest): ApiResult[CreateTutorialBattlesResult] = handleDbException {
    import request._

    if (user.profile.tutorialStates(platform.toString).requestForTutorialBattlesUsed) {
      OkApiResult(CreateTutorialBattlesResult(LimitExceeded))
    } else {

      db.user.setRequestForTutorialBattlesUsed(
        id = user.id,
        platform = platform.toString,
        used = true) ifSome { user =>

        user.stats.solvedQuests.valuesIterator.foldLeft[ApiResult[CreateTutorialBattlesResult]]{
          OkApiResult(CreateTutorialBattlesResult(OK, Some(user.profile)))
        } {
          case (OkApiResult(result), solutionId) =>

            db.solution.readById(solutionId) ifSome { solution =>
              tryCreateBattle(TryCreateBattleRequest(solution = solution, author = user, useTutorialCompetitor = true))
            } map {
              OkApiResult(CreateTutorialBattlesResult(OK, Some(user.profile)))
            }
          case (_ @ badResult, _) =>
            badResult
        }
      }
    }
  }

  /**
   * Reset tutorial script anc completed tutorial tasks.
   */
  def resetTutorial(request: ResetTutorialRequest): ApiResult[ResetTutorialResult] = handleDbException {
    import request._

    db.user.update(
      user.copy(
        profile = user.profile.copy(
          tutorialStates = TutorialPlatform.values.foldLeft[Map[String, TutorialState]](Map.empty){(r, v) => r + (v.toString -> TutorialState())},
          dailyTasks = DailyTasks()
        )
      )
    )

    db.user.readById(user.id) ifSome { user =>
      OkApiResult(ResetTutorialResult(OK, Some(user.profile)))
    }
  }
}

