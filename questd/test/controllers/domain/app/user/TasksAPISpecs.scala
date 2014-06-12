package controllers.domain.app.user

import controllers.domain.BaseAPISpecs
import models.domain._
import models.domain.base._
import java.util.Date
import controllers.domain.app.protocol.ProfileModificationResult

class TasksAPISpecs extends BaseAPISpecs {

  def createUser(dt: DailyTasks) = {
    User(
      id = "user_id",
      profile = Profile(
        dailyTasks = dt,
        ratingToNextLevel = 10000000,
        rights = Rights.full))
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

      val result = api.makeTask(MakeTaskRequest(u, TaskType.Client))

      result.body.get.user must beEqualTo(u)
    }

    "Do nothing if the task should not be completed" in context {
      val u = createUser(DailyTasks(
        tasks = List(
          Task(
            taskType = TaskType.AddToShortList,
            description = "",
            requiredCount = 10,
            currentCount = 0))))

      val result = api.makeTask(MakeTaskRequest(u, TaskType.Client))

      result.body.get.user must beEqualTo(u)
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
            taskType = TaskType.Client,
            description = "",
            requiredCount = 10,
            currentCount = 0),
          Task(
            taskType = TaskType.GiveRewards,
            description = "",
            requiredCount = 5,
            currentCount = 5))))

      db.user.incTask(u.id, TaskType.Client.toString, 0.5f, false) returns Some(u)

      val result = api.makeTask(MakeTaskRequest(u, TaskType.Client))

      there was one(db.user).incTask(u.id, TaskType.Client.toString, 0.5f, false)
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
            taskType = TaskType.Client,
            description = "",
            requiredCount = 10,
            currentCount = 9),
          Task(
            taskType = TaskType.GiveRewards,
            description = "",
            requiredCount = 5,
            currentCount = 5))))

      db.user.incTask(u.id, TaskType.Client.toString, 1f, true) returns Some(u)
      db.user.addToAssets(u.id, r) returns Some(u)

      val result = api.makeTask(MakeTaskRequest(u, TaskType.Client))

      there was one(db.user).addToAssets(u.id, r)
      there was one(db.user).incTask(u.id, TaskType.Client.toString, 1f, true)
    }
  }

}