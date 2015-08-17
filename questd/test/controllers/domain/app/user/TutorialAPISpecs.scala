package controllers.domain.app.user

import controllers.domain.app.protocol.ProfileModificationResult
import controllers.domain.{BaseAPISpecs, OkApiResult}
import models.domain.common.{ClientPlatform, Assets}
import models.domain.tutorial._
import models.domain.tutorialtask.TutorialTask
import models.domain.user.profile._
import org.mockito.Matchers.{eq => mEq}
import org.mockito.Mockito._
import testhelpers.domainstubs._

class TutorialAPISpecs extends BaseAPISpecs {

  def createUser(dt: DailyTasks, assignedTutorialTaskIds: List[String] = List.empty) = {
    createUserStub(
      id = "user_id",
      dailyTasks = dt,
      tutorialState = TutorialState(
        usedTutorialTaskIds = assignedTutorialTaskIds,
        dailyTasksSuppression = false))
  }

  "Tasks API" should {

    "Give reward if everything is completed including tutorial" in context {
      val taskId = "tid"
      val tutorialTaskId = "tuttid"
      val r1 = Assets(10, 20, 30)
      val r2 = Assets(1, 2, 3)
      val u = createUser(DailyTasks(
        reward = r1,
        tasks = List(
          Task(
            taskType = TaskType.AddToFollowing,
            description = "",
            requiredCount = 10,
            currentCount = 10),
          Task(
            taskType = TaskType.LookThroughFriendshipProposals,
            description = "",
            requiredCount = 10,
            currentCount = 10),
          Task(
            taskType = TaskType.GiveRewards,
            description = "",
            requiredCount = 5,
            currentCount = 5),
          Task(
            id = taskId,
            reward = r2,
            taskType = TaskType.Custom,
            description = "",
            requiredCount = 10,
            currentCount = 9,
            tutorialTaskId = Some(tutorialTaskId)))))

      val uc = u.copy(profile = u.profile.copy(dailyTasks = u.profile.dailyTasks.copy(tasks =
        u.profile.dailyTasks.tasks.map(t => t.copy(currentCount = t.requiredCount)))))

      db.user.incTask(u.id, taskId) returns Some(uc)
      db.user.addToAssets(u.id, r1) returns Some(uc)
      db.user.addToAssets(u.id, r2) returns Some(uc)
      db.user.setTasksCompletedFraction(any, any) returns Some(uc)
      db.user.setTasksRewardReceived(id = u.id, rewardReceived = true) returns Some(uc)
      db.user.addMessage(mEq(u.id), any) returns Some(uc)

      val result = api.incTutorialTask(IncTutorialTaskRequest(u, tutorialTaskId))

      result must beEqualTo(OkApiResult(IncTutorialTaskResult(ProfileModificationResult.OK, Some(uc.profile))))
      there was one(db.user).incTask(u.id, taskId)
      there was one(db.user).addToAssets(u.id, r1)
      there was one(db.user).addToAssets(u.id, r2)
      there was one(db.user).setTasksRewardReceived(id = u.id, rewardReceived = true)
      there was one(db.user).setTasksCompletedFraction(any, any)
      there were two(db.user).addMessage(mEq(u.id), any)
    }

    "Do not give reward if everything is completed including tutorial but tutorial is not set to trigger reward" in context {
      val taskId = "tid"
      val tutorialTaskId = "tuttid"
      val r1 = Assets(10, 20, 30)
      val r2 = Assets(100, 200, 300)
      val u = createUser(DailyTasks(
        reward = r1,
        tasks = List(
          Task(
            taskType = TaskType.AddToFollowing,
            description = "",
            requiredCount = 10,
            currentCount = 10),
          Task(
            taskType = TaskType.LookThroughFriendshipProposals,
            description = "",
            requiredCount = 10,
            currentCount = 10),
          Task(
            taskType = TaskType.GiveRewards,
            description = "",
            requiredCount = 5,
            currentCount = 5),
          Task(
            id = taskId,
            reward = r2,
            taskType = TaskType.Custom,
            description = "",
            requiredCount = 10,
            currentCount = 9,
            tutorialTaskId = Some(tutorialTaskId),
            triggersReward = false))))

      val uc = u.copy(profile = u.profile.copy(dailyTasks = u.profile.dailyTasks.copy(tasks =
        u.profile.dailyTasks.tasks.map(t => t.copy(currentCount = t.requiredCount)))))

      db.user.incTask(u.id, taskId) returns Some(uc)
      db.user.addToAssets(u.id, r1) returns Some(uc)
      db.user.addToAssets(u.id, r2) returns Some(uc)
      db.user.setTasksCompletedFraction(any, any) returns Some(uc)
      db.user.setTasksRewardReceived(id = u.id, rewardReceived = true) returns Some(uc)
      db.user.addMessage(mEq(u.id), any) returns Some(uc)

      val result = api.incTutorialTask(IncTutorialTaskRequest(u, tutorialTaskId))

      result must beEqualTo(OkApiResult(IncTutorialTaskResult(ProfileModificationResult.OK, Some(uc.profile))))
      there was one(db.user).incTask(u.id, taskId)
      there was one(db.user).addToAssets(u.id, r2)
      there was no(db.user).setTasksRewardReceived(any, any)
      there was one(db.user).setTasksCompletedFraction(any, any)
      there was one(db.user).addMessage(any, any)
    }

    "Report missing tutorial task properly" in context {
      val taskId = "tid"
      val r = Assets(10, 20, 30)
      val u = createUser(DailyTasks(
        reward = r,
        tasks = List(
          Task(
            taskType = TaskType.AddToFollowing,
            description = "",
            requiredCount = 10,
            currentCount = 10),
          Task(
            taskType = TaskType.LookThroughFriendshipProposals,
            description = "",
            requiredCount = 10,
            currentCount = 10),
          Task(
            taskType = TaskType.GiveRewards,
            description = "",
            requiredCount = 5,
            currentCount = 5))))

      val result = api.incTutorialTask(IncTutorialTaskRequest(u, taskId))

      result must beEqualTo(OkApiResult(IncTutorialTaskResult(ProfileModificationResult.OutOfContent, None)))
    }

    "Carry tutorial tasks to next day if all tasks are not completed" in context {
      val r = Assets(10, 20, 30)
      val tutorialTask = Task(
        taskType = TaskType.Custom,
        description = "",
        requiredCount = 10,
        tutorialTaskId = Some("lala"))

      val u = createUser(DailyTasks(
        reward = r,
        tasks = List(
          Task(
            taskType = TaskType.AddToFollowing,
            description = "",
            requiredCount = 10,
            currentCount = 10),
          Task(
            taskType = TaskType.LookThroughFriendshipProposals,
            description = "",
            requiredCount = 10,
            currentCount = 9),
          Task(
            taskType = TaskType.GiveRewards,
            description = "",
            requiredCount = 5,
            currentCount = 5),
          tutorialTask)))

      user.resetTasks(any, any, any) returns Some(u)
      user.addTasks(any, any, any) returns Some(u)

      val result = api.resetDailyTasks(ResetDailyTasksRequest(u))

      result must beEqualTo(OkApiResult(ResetDailyTasksResult(u)))
      there was one(db.user).resetTasks(any, any, any)
      there was one(db.user).addTasks(u.id, List(tutorialTask), null)
    }

    "Do not carry tutorial tasks to next day if all tasks are completed" in context {
      val r = Assets(10, 20, 30)
      val tutorialTask = Task(
        taskType = TaskType.Custom,
        description = "",
        requiredCount = 10,
        currentCount = 10,
        tutorialTaskId = Some("lala"))

      val u = createUser(DailyTasks(
        reward = r,
        rewardReceived = true,
        tasks = List(
          Task(
            taskType = TaskType.AddToFollowing,
            description = "",
            requiredCount = 10,
            currentCount = 10),
          Task(
            taskType = TaskType.LookThroughFriendshipProposals,
            description = "",
            requiredCount = 10,
            currentCount = 9),
          Task(
            taskType = TaskType.GiveRewards,
            description = "",
            requiredCount = 5,
            currentCount = 5),
          tutorialTask)))

      user.resetTasks(any, any, any) returns Some(u)

      val result = api.resetDailyTasks(ResetDailyTasksRequest(u))

      result must beEqualTo(OkApiResult(ResetDailyTasksResult(u)))
      there was one(db.user).resetTasks(any, any, any)
      there was no(db.user).addTasks(any, any, any)
    }

    "Do not assign already assigned tutorial task" in context {
      val tutorialTaskId = "ttid"
      val u = createUser(DailyTasks(
        reward = Assets(1, 2, 3),
        rewardReceived = true,
        tasks = List.empty), List(tutorialTaskId))

      val result = api.assignTutorialTask(AssignTutorialTaskRequest(u, ClientPlatform.iPhone, tutorialTaskId))

      result must beEqualTo(OkApiResult(AssignTutorialTaskResult(ProfileModificationResult.LimitExceeded)))
      there was no(db.user).setTasksCompletedFraction(any, any)
    }

    "Do not assign tutorial ask what is not exists" in context {
      val tutorialTaskId = "ttid"
      val u = createUser(DailyTasks(
        reward = Assets(1, 2, 3),
        tasks = List.empty), List.empty)

      db.tutorialTask.readById(s"a$tutorialTaskId") returns None

      val result = api.assignTutorialTask(AssignTutorialTaskRequest(u, ClientPlatform.iPhone, s"a$tutorialTaskId"))

      result must beEqualTo(OkApiResult(AssignTutorialTaskResult(ProfileModificationResult.OutOfContent)))
      there was one(db.tutorialTask).readById(any)
      there was no(db.user).setTasksCompletedFraction(any, any)
    }

    "Assign tutorial task" in context {
      val tutorialTaskId = "ttid"
      val u = createUser(DailyTasks(
        reward = Assets(1, 2, 3),
        rewardReceived = true,
        tasks = List.empty), List.empty)

      db.tutorialTask.readById(tutorialTaskId) returns Some(
        TutorialTask(
          id = "taskId",
          taskType = TaskType.Custom,
          description = "",
          requiredCount = 10,
          reward = Assets(rating = 10),
          triggersReward = false))
      db.user.resetTasks(any, any, any) returns Some(u)
      db.user.addTutorialTaskAssigned(any, any, any) returns Some(u)
      db.user.addTasks(any, any, any) returns Some(u)
      db.user.setTasksCompletedFraction(any, any) returns Some(u)

      val result = api.assignTutorialTask(AssignTutorialTaskRequest(u, ClientPlatform.iPhone, tutorialTaskId))

      result must beEqualTo(OkApiResult(AssignTutorialTaskResult(ProfileModificationResult.OK, Some(u.profile))))
      there was one(db.tutorialTask).readById(tutorialTaskId)
      there was one(db.user).resetTasks(any, any, any)
      there was one(db.user).addTutorialTaskAssigned(any, any, any)
      there was one(db.user).addTasks(any, any, mEq(Some(Assets(rating = 10))))
      there was one(db.user).setTasksCompletedFraction(any, any)
    }
  }

  "Assign tutorial quest" in context {
    val tutorialQuestId = "tutorial_1"
    val u = createUserStub()
    val q = createQuestStub(id = tutorialQuestId)

    quest.readById(tutorialQuestId) returns Some(q)
    user.addTutorialQuestAssigned(any, any, any) returns Some(u)
    user.addEntryToTimeLine(any, any) returns Some(u)

    val result = api.assignTutorialQuest(AssignTutorialQuestRequest(u, ClientPlatform.iPhone, tutorialQuestId))

    result must beAnInstanceOf[OkApiResult[AssignTutorialQuestResult]]

    there was one(quest).readById(tutorialQuestId)
    there was one(user).addTutorialQuestAssigned(mEq(u.id), any, mEq(tutorialQuestId))
    there was one(user).addEntryToTimeLine(any, any)
  }

  "Do not assign tutorial quest if it was assigned" in context {
    val tutorialQuestId = "tqid"
    val u = createUserStub(tutorialState = TutorialState(usedTutorialQuestIds = List(tutorialQuestId)))

    val result = api.assignTutorialQuest(AssignTutorialQuestRequest(u, ClientPlatform.iPhone, tutorialQuestId))

    result must beEqualTo(OkApiResult(AssignTutorialQuestResult(ProfileModificationResult.LimitExceeded)))

    there was no(quest).readById(tutorialQuestId)
    there was no(user).addTutorialQuestAssigned(mEq(u.id), any, mEq(tutorialQuestId))
    there was no(user).addEntryToTimeLine(any, any)
  }

  "closeTutorialElement calls server action"  in context {
    val elementId = "elementId"
    val u = createUserStub()
    val tut = Tutorial(
      id = ClientPlatform.iPhone.toString,
      elements = List(TutorialElement(
        id = elementId,
        actions = List.empty,
        triggers = List.empty,
        serverActions = List(TutorialServerAction(
          actionType = TutorialServerActionType.Dummy
        ))
      ))
    )

    tutorial.readById(any) returns Some(tut)
    doReturn(OkApiResult(ExecuteServerTutorialActionResult(u))).when(api).executeServerTutorialAction(any)
    user.addClosedTutorialElement(any, any, any) returns Some(u)

    val result = api.closeTutorialElement(CloseTutorialElementRequest(u, ClientPlatform.iPhone, elementId))

    result must beAnInstanceOf[OkApiResult[CloseTutorialElementResult]]
    there was one(api).executeServerTutorialAction(any)
  }

  "executeServerTutorialAction executes RemoveDailyTasksSuppression"  in context {
    user.setDailyTasksSuppressed(
      id = any,
      platform = any,
      suppressed = mEq(false)) returns Some(createUserStub())

    val result = api.executeServerTutorialAction(ExecuteServerTutorialActionRequest(
      createUserStub(),
      ClientPlatform.iPhone,
      TutorialServerAction(actionType = TutorialServerActionType.RemoveDailyTasksSuppression)))

    result must beAnInstanceOf[OkApiResult[ExecuteServerTutorialActionResult]]
    there was one(user).setDailyTasksSuppressed(
      id = any,
      platform = any,
      suppressed = mEq(false))
  }

  "executeServerTutorialAction executes AssignDailyTasks"  in context {
    val u = createUserStub()

    doReturn(OkApiResult(AssignDailyTasksResult(u))).when(api).assignDailyTasks(any)

    val result = api.executeServerTutorialAction(ExecuteServerTutorialActionRequest(
      createUserStub(),
      ClientPlatform.iPhone,
      TutorialServerAction(actionType = TutorialServerActionType.AssignDailyTasks)))

    result must beAnInstanceOf[OkApiResult[ExecuteServerTutorialActionResult]]
    there was one(api).assignDailyTasks(any)
  }
}

