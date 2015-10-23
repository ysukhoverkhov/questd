package controllers.domain.app.user

import components._
import controllers.domain.app.protocol.CommonCode
import controllers.domain.helpers._
import controllers.domain.{DomainAPIComponent, _}
import models.domain.common.{Assets, ClientPlatform}
import models.domain.tutorial.{TutorialElement, TutorialServerAction, TutorialServerActionType}
import models.domain.user._
import models.domain.user.profile.{DailyTasks, Profile, Task, TutorialState}
import models.domain.user.timeline.{TimeLineReason, TimeLineType}
import play.Logger

case class GetCommonTutorialRequest(platform: ClientPlatform.Value)
case class GetCommonTutorialResult(tutorialElements: List[TutorialElement])

case class GetTutorialRequest(user: User, platform: ClientPlatform.Value)
case class GetTutorialResult(tutorialElements: List[TutorialElement])

object CloseTutorialElementCode extends Enumeration with CommonCode {
}
case class CloseTutorialElementRequest(user: User, platform: ClientPlatform.Value, elementId: String)
case class CloseTutorialElementResult(
  allowed: CloseTutorialElementCode.Value,
  profile: Option[Profile] = None)

object AssignTutorialTaskCode extends Enumeration with CommonCode {
  val TaskAlreadyAssigned = Value
  val TaskNotFound = Value
}
case class AssignTutorialTaskRequest(user: User, platform: ClientPlatform.Value, taskId: String)
case class AssignTutorialTaskResult(
  allowed: AssignTutorialTaskCode.Value,
  profile: Option[Profile] = None)

object IncTutorialTaskCode extends Enumeration with CommonCode {
  val TaskNotFound = Value
}
case class IncTutorialTaskRequest(user: User, taskId: String)
case class IncTutorialTaskResult(
  allowed: IncTutorialTaskCode.Value,
  profile: Option[Profile] = None)

object AssignTutorialQuestCode extends Enumeration with CommonCode {
  val QuestAlreadyAssigned = Value
  val QuestNotFound = Value
}
case class AssignTutorialQuestRequest(user: User, platform: ClientPlatform.Value, questId: String)
case class AssignTutorialQuestResult(
  allowed: AssignTutorialQuestCode.Value,
  profile: Option[Profile] = None)

object ResetTutorialCode extends Enumeration with CommonCode {
}
case class ResetTutorialRequest(user: User)
case class ResetTutorialResult(
  allowed: ResetTutorialCode.Value,
  profile: Option[Profile] = None)

case class ExecuteServerTutorialActionRequest(user: User, platform: ClientPlatform.Value, serverAction: TutorialServerAction)
case class ExecuteServerTutorialActionResult(user: User)


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
    import CloseTutorialElementCode._
    import request._

    db.tutorial.readById(platform.toString) ifSome { tutorial =>
      tutorial.elements.find(_.id == elementId) ifSome { element =>
        element.serverActions.foldLeft[ApiResult[ExecuteServerTutorialActionResult]] {
          OkApiResult(ExecuteServerTutorialActionResult(user))
        } {
          case (OkApiResult(ExecuteServerTutorialActionResult(u)), serverAction) =>
            executeServerTutorialAction(ExecuteServerTutorialActionRequest(u, platform, serverAction))
          case (result, _) =>
            result
        }
      }
    } map { result =>
      db.user.addClosedTutorialElement(user.id, platform.toString, elementId) ifSome { v =>
        OkApiResult(CloseTutorialElementResult(OK, Some(v.profile)))
      }
    }
  }

  /**
   * Executing server tutorial action
   */
  def executeServerTutorialAction(request: ExecuteServerTutorialActionRequest): ApiResult[ExecuteServerTutorialActionResult] = handleDbException {
    import request._

    serverAction.actionType match {
      case TutorialServerActionType.RemoveDailyTasksSuppression =>
        db.user.setDailyTasksSuppressed(
          id = user.id,
          platform = platform.toString,
          suppressed = false) ifSome { user =>
          OkApiResult(ExecuteServerTutorialActionResult(user))
        }

      case TutorialServerActionType.AssignDailyTasks =>
        assignDailyTasks(AssignDailyTasksRequest(user)) map { r =>
          OkApiResult(ExecuteServerTutorialActionResult(r.user))
        }

      case TutorialServerActionType.Dummy =>
        OkApiResult(ExecuteServerTutorialActionResult(user))

      case _ @ action =>
        Logger.error(s"Unknown server tutorial action $action")
        OkApiResult(ExecuteServerTutorialActionResult(user))

    }
  }

  /**
   * Assign client task to user.
   */
  def assignTutorialTask(request: AssignTutorialTaskRequest): ApiResult[AssignTutorialTaskResult] = handleDbException {
    import AssignTutorialTaskCode._
    import request._

    // 1. check is the task was already given.
    if (user.profile.tutorialStates(platform.toString).usedTutorialTaskIds.contains(taskId)) {
      OkApiResult(AssignTutorialTaskResult(TaskAlreadyAssigned))
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
          OkApiResult(AssignTutorialTaskResult(TaskNotFound))
      }
    }
  }

  /**
   * Inc by one progress of tutorial task.
   */
  def incTutorialTask(request: IncTutorialTaskRequest): ApiResult[IncTutorialTaskResult] = handleDbException {
    import IncTutorialTaskCode._
    import request._

    user.profile.dailyTasks.tasks.find(t => t.tutorialTaskId.contains(taskId)).fold[ApiResult[IncTutorialTaskResult]] {
      OkApiResult(IncTutorialTaskResult(TaskNotFound))
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
    import AssignTutorialQuestCode._
    import request._

    if (user.profile.tutorialStates(platform.toString).usedTutorialQuestIds.contains(questId)) {
      OkApiResult(AssignTutorialQuestResult(QuestAlreadyAssigned))
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
          OkApiResult(AssignTutorialQuestResult(QuestNotFound))
      }
    }
  }

  /**
   * Reset tutorial script anc completed tutorial tasks.
   */
  def resetTutorial(request: ResetTutorialRequest): ApiResult[ResetTutorialResult] = handleDbException {
    import ResetTutorialCode._
    import request._

    db.user.update(
      user.copy(
        profile = user.profile.copy(
          tutorialStates = ClientPlatform.values.foldLeft[Map[String, TutorialState]](Map.empty){(r, v) => r + (v.toString -> TutorialState())},
          dailyTasks = DailyTasks()
        )
      )
    )

    db.user.readById(user.id) ifSome { user =>
      OkApiResult(ResetTutorialResult(OK, Some(user.profile)))
    }
  }
}

