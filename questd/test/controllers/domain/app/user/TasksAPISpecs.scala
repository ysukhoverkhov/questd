package controllers.domain.app.user

import controllers.domain.app.protocol.ProfileModificationResult
import controllers.domain.{BaseAPISpecs, OkApiResult}
import models.domain.common.{ClientPlatform, Assets}
import models.domain.user._
import models.domain.user.profile._
import org.mockito.Matchers.{eq => mEq}

class TasksAPISpecs extends BaseAPISpecs {

  def createUser(dt: DailyTasks, assignedTutorialTaskIds: List[String] = List.empty) = {
    User(
      id = "user_id",
      profile = Profile(
        dailyTasks = dt,
        ratingToNextLevel = 10000000,
        rights = Rights.full,
        tutorialStates = Map(ClientPlatform.iPhone.toString -> TutorialState(
          usedTutorialTaskIds = assignedTutorialTaskIds))))
  }

  "Tasks API" should {

    "Do nothing if task is already completed" in context {
      val u = createUser(
        DailyTasks(
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
      val u = createUser(
        DailyTasks(
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
      val u = createUser(
        DailyTasks(
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

      result must beEqualTo(OkApiResult(IncTutorialTaskResult(IncTutorialTaskCode.OK, Some(u.profile))))
    }

    "Calculate completed percent correctly" in context {
      val taskId = "asdasjdkas"
      def createUserInternal(cc: Int) = {
        createUser(
          DailyTasks(
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
        createUser(
          DailyTasks(
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
      val u = createUser(
        DailyTasks(
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
      val uc = u.copy(
        profile = u.profile.copy(
          dailyTasks = u.profile.dailyTasks.copy(
            tasks =
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
  }
}

