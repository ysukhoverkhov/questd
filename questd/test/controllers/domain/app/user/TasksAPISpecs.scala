package controllers.domain.app.user

import controllers.domain.app.protocol.ProfileModificationResult
import controllers.domain.{BaseAPISpecs, OkApiResult}
import models.domain.common.Assets
import models.domain.tutorial.TutorialPlatform
import models.domain.tutorialtask.TutorialTask
import models.domain.user._
import models.domain.user.profile._
import org.mockito.Matchers.{eq => mEq}
import testhelpers.domainstubs._

class TasksAPISpecs extends BaseAPISpecs {

  def createUser(dt: DailyTasks, assignedTutorialTaskIds: List[String] = List.empty) = {
    User(
      id = "user_id",
      profile = Profile(
        dailyTasks = dt,
        ratingToNextLevel = 10000000,
        rights = Rights.full,
        tutorialStates = Map(TutorialPlatform.iPhone.toString -> TutorialState(
          usedTutorialTaskIds = assignedTutorialTaskIds))))
  }

  "Tasks API" should {

    "Do nothing if task is already completed" in context {
      val u = createUser(DailyTasks(
        tasks = List(
          Task(
            taskType = TaskType.Custom,
            description = "",
            requiredCount = 10,
            currentCount = 10))))

      val result = api.makeTask(MakeTaskRequest(u, taskType = Some(TaskType.Custom)))

      result must beEqualTo(OkApiResult(MakeTaskResult(u)))
    }

    "Do nothing if regular task should not be completed" in context {
      val u = createUser(DailyTasks(
        tasks = List(
          Task(
            taskType = TaskType.AddToFollowing,
            description = "",
            requiredCount = 10,
            currentCount = 0),
          Task(
            taskType = TaskType.CreateQuest,
            description = "",
            requiredCount = 10,
            currentCount = 10,
            tutorialTaskId = None))))

      val result = api.makeTask(MakeTaskRequest(u, taskType = Some(TaskType.GiveRewards)))

      result must beEqualTo(OkApiResult(MakeTaskResult(u)))
    }

    "Do nothing if tutorial task should not be completed" in context {
      val taskId = "tid"
      val u = createUser(DailyTasks(
        tasks = List(
          Task(
            taskType = TaskType.AddToFollowing,
            description = "",
            requiredCount = 10,
            currentCount = 0),
          Task(
            taskType = TaskType.Custom,
            description = "",
            requiredCount = 10,
            currentCount = 10,
            tutorialTaskId = Some(taskId)))))

      val result = api.incTutorialTask(IncTutorialTaskRequest(u, taskId = taskId))

      result must beEqualTo(OkApiResult(IncTutorialTaskResult(ProfileModificationResult.OK, Some(u.profile))))
    }

    "Calculate completed percent correctly" in context {
      val taskId = "asdasjdkas"
      def createUserInternal(cc: Int) = {
        createUser(DailyTasks(
          tasks = List(
            Task(
              taskType = TaskType.AddToFollowing,
              description = "",
              requiredCount = 10,
              currentCount = cc),
            Task(
              id = taskId,
              taskType = TaskType.LookThroughFriendshipProposals,
              description = "",
              requiredCount = 10,
              currentCount = 0),
            Task(
              taskType = TaskType.GiveRewards,
              description = "",
              requiredCount = 5,
              currentCount = 5),
            Task(
              taskType = TaskType.Custom,
              description = "",
              requiredCount = 10,
              currentCount = 5,
              tutorialTaskId = Some("lala")))))
      }

      val u = createUserInternal(4)

      db.user.incTask(u.id, taskId) returns Some(createUserInternal(5))
      db.user.setTasksCompletedFraction(any, any) returns Some(createUserInternal(5))

      val result = api.makeTask(MakeTaskRequest(u, taskType = Some(TaskType.LookThroughFriendshipProposals)))

      result must beAnInstanceOf[OkApiResult[MakeTaskResult]]

      there was one(db.user).incTask(u.id, taskId)
      there was one(db.user).setTasksCompletedFraction(any, mEq(0.5f))
    }

    "Inc several tasks at once" in context {
      def createUserInternal(cc: Int) = {
        createUser(DailyTasks(
          tasks = List(
            Task(
              taskType = TaskType.AddToFollowing,
              description = "",
              requiredCount = 10,
              currentCount = cc),
            Task(
              taskType = TaskType.LookThroughFriendshipProposals,
              description = "",
              requiredCount = 10,
              currentCount = 0),
            Task(
              taskType = TaskType.AddToFollowing,
              description = "",
              requiredCount = 10,
              currentCount = cc),
            Task(
              taskType = TaskType.Custom,
              description = "",
              requiredCount = 10,
              currentCount = 5,
              tutorialTaskId = Some("lala")))))
      }

      val u = createUserInternal(4)

      db.user.incTask(any, any) returns Some(u)
      db.user.setTasksCompletedFraction(any, any) returns Some(u)

      val result = api.makeTask(MakeTaskRequest(u, taskType = Some(TaskType.AddToFollowing)))
      result must beEqualTo(OkApiResult(MakeTaskResult(u)))

      there were two(db.user).incTask(any, any)
    }

    "Give reward if everything is completed" in context {
      val taskId = "asdasjdkas"
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
            id = taskId,
            taskType = TaskType.LookThroughFriendshipProposals,
            description = "",
            requiredCount = 10,
            currentCount = 9,
            reward = r2),
          Task(
            taskType = TaskType.GiveRewards,
            description = "",
            requiredCount = 5,
            currentCount = 5),
          Task(
            taskType = TaskType.Custom,
            description = "",
            requiredCount = 10,
            currentCount = 10,
            tutorialTaskId = Some("lala")))))
      val uc = u.copy(profile = u.profile.copy(dailyTasks = u.profile.dailyTasks.copy(tasks =
        u.profile.dailyTasks.tasks.map(t => t.copy(currentCount = t.requiredCount)))))

      db.user.incTask(u.id, taskId) returns Some(uc)
      db.user.addToAssets(u.id, r1) returns Some(uc)
      db.user.addToAssets(u.id, r2) returns Some(uc)
      db.user.setTasksCompletedFraction(any, any) returns Some(uc)
      db.user.setTasksRewardReceived(id = u.id, rewardReceived = true) returns Some(uc)
      db.user.addMessage(mEq(u.id), any) returns Some(uc)

      val result = api.makeTask(MakeTaskRequest(u, taskType = Some(TaskType.LookThroughFriendshipProposals)))

      result must beEqualTo(OkApiResult(MakeTaskResult(uc)))
      there was one(db.user).incTask(u.id, taskId)
      there was one(db.user).addToAssets(u.id, r1)
      there was one(db.user).addToAssets(u.id, r2)
      there was one(db.user).setTasksRewardReceived(id = u.id, rewardReceived = true)
      there was one(db.user).setTasksCompletedFraction(any, any)
      there were two(db.user).addMessage(mEq(u.id), any)
    }

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

      val result = api.assignTutorialTask(AssignTutorialTaskRequest(u, TutorialPlatform.iPhone, tutorialTaskId))

      result must beEqualTo(OkApiResult(AssignTutorialTaskResult(ProfileModificationResult.LimitExceeded)))
      there was no(db.user).setTasksCompletedFraction(any, any)
    }

    "Do not assign tutorial ask what is not exists" in context {
      val tutorialTaskId = "ttid"
      val u = createUser(DailyTasks(
        reward = Assets(1, 2, 3),
        tasks = List.empty), List.empty)

      db.tutorialTask.readById(s"a$tutorialTaskId") returns None

      val result = api.assignTutorialTask(AssignTutorialTaskRequest(u, TutorialPlatform.iPhone, s"a$tutorialTaskId"))

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

      val result = api.assignTutorialTask(AssignTutorialTaskRequest(u, TutorialPlatform.iPhone, tutorialTaskId))

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

    val result = api.assignTutorialQuest(AssignTutorialQuestRequest(u, TutorialPlatform.iPhone, tutorialQuestId))

    result must beAnInstanceOf[OkApiResult[AssignTutorialQuestResult]]

    there was one(quest).readById(tutorialQuestId)
    there was one(user).addTutorialQuestAssigned(mEq(u.id), any, mEq(tutorialQuestId))
    there was one(user).addEntryToTimeLine(any, any)
  }

  "Do not assign tutorial quest if it was assigned" in context {
    val tutorialQuestId = "tqid"
    val u = createUserStub(tutorialState = TutorialState(usedTutorialQuestIds = List(tutorialQuestId)))

    val result = api.assignTutorialQuest(AssignTutorialQuestRequest(u, TutorialPlatform.iPhone, tutorialQuestId))

    result must beEqualTo(OkApiResult(AssignTutorialQuestResult(ProfileModificationResult.LimitExceeded)))

    there was no(quest).readById(tutorialQuestId)
    there was no(user).addTutorialQuestAssigned(mEq(u.id), any, mEq(tutorialQuestId))
    there was no(user).addEntryToTimeLine(any, any)
  }

}

