package models.store.mongo.user

import java.util.Date

import models.domain.common.Assets
import models.domain.user.User
import models.domain.user.profile.{Profile, TaskType, Task, DailyTasks}
import models.store.mongo.BaseDAOSpecs
import play.api.test.WithApplication

/**
 * MongoUserTasksDAO specs
 */
trait MongoUserTasksDAOSpecs { this: BaseDAOSpecs =>

  "Mongo User DAO" should {
    "incTask should increase number of times task was completed by one" in new WithApplication(appWithTestDatabase) {
      val userid = "incTasks"
      db.user.create(User(userid))

      val tasks = DailyTasks(
        tasks = List(
          Task(
            taskType = TaskType.Custom,
            description = "",
            requiredCount = 10),
          Task(
            taskType = TaskType.GiveRewards,
            description = "",
            requiredCount = 10)))

      db.user.resetTasks(userid, tasks, new Date())

      db.user.incTask(id = userid, taskId = tasks.tasks.head.id)
      db.user.incTask(id = userid, taskId = tasks.tasks(1).id)
      db.user.incTask(id = userid, taskId = tasks.tasks(1).id)

      val ou = db.user.readById(userid)
      ou must beSome.which((u: User) => u.id.toString == userid)
      ou must beSome
        .which((u: User) => u.profile.dailyTasks.tasks.filter(_.taskType == TaskType.Custom).head.currentCount == 1)
      ou must beSome.which(
        (u: User) => u.profile.dailyTasks.tasks.filter(_.taskType == TaskType.GiveRewards).head.currentCount == 2)
    }

    "setTasksCompletedFraction should change percentage completed" in new WithApplication(appWithTestDatabase) {
      val userid = "incTasks2"
      val fraction = 0.3f
      db.user.create(User(userid))

      val tasks = DailyTasks(
        tasks = List(
          Task(
            taskType = TaskType.Custom,
            description = "",
            requiredCount = 10)))

      db.user.resetTasks(userid, tasks, new Date())

      db.user.setTasksCompletedFraction(userid, fraction)

      val ou = db.user.readById(userid)
      ou must beSome.which((u: User) => u.id.toString == userid)
      ou.get.profile.dailyTasks.completed must beEqualTo(fraction)
      ou.get.profile.dailyTasks.rewardReceived must beEqualTo(false)
    }

    "setTasksRewardReceived should change the flag" in new WithApplication(appWithTestDatabase) {
      val userid = "incTasks2"
      db.user.create(User(userid))

      val tasks = DailyTasks(
        tasks = List(
          Task(
            taskType = TaskType.Custom,
            description = "",
            requiredCount = 10)))

      db.user.resetTasks(userid, tasks, new Date())

      db.user.setTasksRewardReceived(id = userid, rewardReceived = true)

      val ou = db.user.readById(userid)
      ou must beSome.which((u: User) => u.id.toString == userid)
      ou.get.profile.dailyTasks.rewardReceived must beEqualTo(true)
    }

    "addTasks works" in new WithApplication(appWithTestDatabase) {

      def t = {
        Task(taskType = TaskType.GiveRewards, description = "d", requiredCount = 1)
      }

      val userid = "addTasksTest"

      db.user.delete(userid)
      db.user.create(User(
        id = userid,
        profile = Profile(
          dailyTasks = DailyTasks(
            tasks = List(t, t, t),
            reward = Assets(1, 2, 3)))))

      val ou = db.user.addTasks(userid, List(t, t), Some(Assets(rating = 10)))

      ou must beSome.which((u: User) => u.id.toString == userid)
      ou must beSome.which((u: User) => u.profile.dailyTasks.tasks.length == 5)
      ou must beSome.which((u: User) => u.profile.dailyTasks.reward == Assets(1, 2, 13))
    }
  }
}
