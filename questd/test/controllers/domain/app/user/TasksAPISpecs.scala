package controllers.domain.app.user

import controllers.domain.BaseAPISpecs
import models.domain._
import controllers.domain.app.protocol.ProfileModificationResult
import controllers.domain.OkApiResult

class TasksAPISpecs extends BaseAPISpecs {

  def createUser(dt: DailyTasks, assignedTutorialTaskIds: List[String] = List()) = {
    User(
      id = "user_id",
      profile = Profile(
        dailyTasks = dt,
        ratingToNextLevel = 10000000,
        rights = Rights.full),
      tutorial = TutorialState(
        assignedTutorialTaskIds = assignedTutorialTaskIds))
  }

  "Tasks API" should {

    "Do nothing if task is already completed" in context {
      val u = createUser(DailyTasks(
        tasks = List(
          Task(
            taskType = TaskType.Client,
            description = "",
            requiredCount = 10,
            currentCount = 10))))

      val result = api.makeTask(MakeTaskRequest(u, taskType = Some(TaskType.Client)))

      result.body.get.user must beEqualTo(u)
    }

    "Do nothing if regular task should not be completed" in context {
      val u = createUser(DailyTasks(
        tasks = List(
          Task(
            taskType = TaskType.AddToShortList,
            description = "",
            requiredCount = 10,
            currentCount = 0),
          Task(
            taskType = TaskType.Client,
            description = "",
            requiredCount = 10,
            currentCount = 10,
            tutorialTask = Some(TutorialTask(
              id = "taskId",
              taskType = TaskType.Client,
              description = "",
              requiredCount = 10,
              reward = Assets()))))))

      val result = api.makeTask(MakeTaskRequest(u, taskType = Some(TaskType.GiveRewards)))

      result.body.get.user must beEqualTo(u)
    }

    "Do nothing if tutorial task should not be completed" in context {
      val taskId = "tid"
      val u = createUser(DailyTasks(
        tasks = List(
          Task(
            taskType = TaskType.AddToShortList,
            description = "",
            requiredCount = 10,
            currentCount = 0),
          Task(
            taskType = TaskType.Client,
            description = "",
            requiredCount = 10,
            currentCount = 10,
            tutorialTask = Some(TutorialTask(
              id = taskId,
              taskType = TaskType.Client,
              description = "",
              requiredCount = 10,
              reward = Assets()))))))

      val result = api.incTutorialTask(IncTutorialTaskRequest(u, taskId = taskId))

      result must beEqualTo(OkApiResult(IncTutorialTaskResult(ProfileModificationResult.OK, Some(u.profile))))
    }

    "Calculate completed percent correctly" in context {
      val u = createUser(DailyTasks(
        tasks = List(
          Task(
            taskType = TaskType.AddToShortList,
            description = "",
            requiredCount = 10,
            currentCount = 4),
          Task(
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
            taskType = TaskType.Client,
            description = "",
            requiredCount = 10,
            currentCount = 5,
            tutorialTask = Some(TutorialTask(
              id = "taskId",
              taskType = TaskType.Client,
              description = "",
              requiredCount = 10,
              reward = Assets()))))))

      db.user.incTask(u.id, TaskType.LookThroughFriendshipProposals.toString, 0.5f, rewardReceived = false) returns Some(u)

      val result = api.makeTask(MakeTaskRequest(u, taskType = Some(TaskType.LookThroughFriendshipProposals)))

      there was one(db.user).incTask(u.id, TaskType.LookThroughFriendshipProposals.toString, 0.5f, rewardReceived = false)
    }

    "Give reward if everything is completed" in context {
      val r = Assets(10, 20, 30)
      val u = createUser(DailyTasks(
        reward = r,
        tasks = List(
          Task(
            taskType = TaskType.AddToShortList,
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
          Task(
            taskType = TaskType.Client,
            description = "",
            requiredCount = 10,
            currentCount = 10,
            tutorialTask = Some(TutorialTask(
              id = "taskId",
              taskType = TaskType.Client,
              description = "",
              requiredCount = 10,
              reward = r))))))

      db.user.incTask(u.id, TaskType.LookThroughFriendshipProposals.toString, 1f, rewardReceived = true) returns Some(u)
      db.user.addToAssets(u.id, r) returns Some(u)

      val result = api.makeTask(MakeTaskRequest(u, taskType = Some(TaskType.LookThroughFriendshipProposals)))

      result must beEqualTo(OkApiResult(MakeTaskResult(u)))
      there was one(db.user).addToAssets(u.id, r)
      there was one(db.user).incTask(u.id, TaskType.LookThroughFriendshipProposals.toString, 1f, rewardReceived = true)
    }

    "Give reward if everything is completed including tutorial" in context {
      val taskId = "tid"
      val r = Assets(10, 20, 30)
      val u = createUser(DailyTasks(
        reward = r,
        tasks = List(
          Task(
            taskType = TaskType.AddToShortList,
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
            taskType = TaskType.Client,
            description = "",
            requiredCount = 10,
            currentCount = 9,
            tutorialTask = Some(TutorialTask(
              id = taskId,
              taskType = TaskType.Client,
              description = "",
              requiredCount = 10,
              reward = r))))))

      db.user.incTutorialTask(u.id, taskId, 1f, rewardReceived = true) returns Some(u)
      db.user.addToAssets(u.id, r) returns Some(u)

      val result = api.incTutorialTask(IncTutorialTaskRequest(u, taskId))

      result must beEqualTo(OkApiResult(IncTutorialTaskResult(ProfileModificationResult.OK, Some(u.profile))))
      there was one(db.user).addToAssets(u.id, r)
      there was one(db.user).incTutorialTask(u.id, taskId, 1f, rewardReceived = true)
    }

    "Report missing tutorial task properly" in context {
      val taskId = "tid"
      val r = Assets(10, 20, 30)
      val u = createUser(DailyTasks(
        reward = r,
        tasks = List(
          Task(
            taskType = TaskType.AddToShortList,
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
      there was no(db.user).addToAssets(u.id, r)
      there was no(db.user).incTutorialTask(u.id, taskId, 1f, rewardReceived = true)
    }

    "Carry tutorial tasks to next day if all tasks are not completed" in context {
      val r = Assets(10, 20, 30)
      val tr = Assets(1, 2, 3)
      val tutoralTask = Task(
        taskType = TaskType.Client,
        description = "",
        requiredCount = 10,
        tutorialTask = Some(TutorialTask(
          id = "taskId",
          taskType = TaskType.Client,
          description = "",
          requiredCount = 10,
          reward = tr)))

      val u = createUser(DailyTasks(
        reward = r,
        tasks = List(
          Task(
            taskType = TaskType.AddToShortList,
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
          tutoralTask)))

      user.resetTasks(any, any, any) returns Some(u)
      user.addTasks(any, any, any) returns Some(u)

      val result = api.resetDailyTasks(ResetDailyTasksRequest(u))

      result must beEqualTo(OkApiResult(ResetDailyTasksResult()))
      there was one(db.user).resetTasks(any, any, any)
      there was one(db.user).addTasks(u.id, List(tutoralTask), tr)
    }

    "Do not carry tutorial tasks to next day if all tasks are completed" in context {
      val r = Assets(10, 20, 30)
      val tr = Assets(1, 2, 3)
      val tutoralTask = Task(
        taskType = TaskType.Client,
        description = "",
        requiredCount = 10,
        tutorialTask = Some(TutorialTask(
          id = "taskId",
          taskType = TaskType.Client,
          description = "",
          requiredCount = 10,
          reward = tr)))

      val u = createUser(DailyTasks(
        reward = r,
        rewardReceived = true,
        tasks = List(
          Task(
            taskType = TaskType.AddToShortList,
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
          tutoralTask)))

      user.resetTasks(any, any, any) returns Some(u)

      val result = api.resetDailyTasks(ResetDailyTasksRequest(u))

      result must beEqualTo(OkApiResult(ResetDailyTasksResult()))
      there was one(db.user).resetTasks(any, any, any)
      there was no(db.user).addTasks(any, any, any)
    }

    "Do not assign already assigned tutorial ask" in context {
      val tutorialTaskId = "ttid"
      val u = createUser(DailyTasks(
        reward = Assets(1, 2, 3),
        rewardReceived = true,
        tasks = List()), List(tutorialTaskId))

      val result = api.assignTutorialTask(AssignTutorialTaskRequest(u, tutorialTaskId))

      result must beEqualTo(OkApiResult(AssignTutorialTaskResult(ProfileModificationResult.LimitExceeded)))
    }

    "Do not assign tutorial ask what is not exists" in context {
      val tutorialTaskId = "ttid"
      val u = createUser(DailyTasks(
        reward = Assets(1, 2, 3),
        tasks = List()), List())

      db.tutorialTask.readById(s"a$tutorialTaskId") returns None

      val result = api.assignTutorialTask(AssignTutorialTaskRequest(u, s"a$tutorialTaskId"))

      result must beEqualTo(OkApiResult(AssignTutorialTaskResult(ProfileModificationResult.OutOfContent)))
      there was one(db.tutorialTask).readById(any)
    }

    "Assign tutorial task" in context {
      val tutorialTaskId = "ttid"
      val u = createUser(DailyTasks(
        reward = Assets(1, 2, 3),
        rewardReceived = true,
        tasks = List()), List())

      db.tutorialTask.readById(tutorialTaskId) returns Some(
        TutorialTask(
          id = "taskId",
          taskType = TaskType.Client,
          description = "",
          requiredCount = 10,
          reward = Assets()))
      db.user.resetTasks(any, any, any) returns Some(u)
      db.user.addTutorialTaskAssigned(any, any) returns Some(u)
      db.user.addTasks(any, any, any) returns Some(u)

      val result = api.assignTutorialTask(AssignTutorialTaskRequest(u, tutorialTaskId))

      result must beEqualTo(OkApiResult(AssignTutorialTaskResult(ProfileModificationResult.OK, Some(u.profile))))
      there was one(db.tutorialTask).readById(tutorialTaskId)
      there was one(db.user).resetTasks(any, any, any)
      there was one(db.user).addTutorialTaskAssigned(any, any)
      there was one(db.user).addTasks(any, any, any)
    }

  }
}

