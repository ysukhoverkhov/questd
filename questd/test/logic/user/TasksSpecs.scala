package logic.user

import models.domain._

class TasksSpecs extends BaseUserLogicSpecs {

  "Tasks Logic" should {

    "Generate DailyTasks on request" in {

      val u = User()
      val dailyResult = u.getTasksForTomorrow

      dailyResult.tasks.length must beGreaterThan(0)
    }
  }

}

