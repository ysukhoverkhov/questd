package logic.user

import models.domain._

class TasksSpecs extends BaseUserLogicSpecs {

  "Tasks Logic" should {

    "Generate DailyTasks on request" in {

      val u = User()
      val dailyResult = u.getTasksForTomorrow

      dailyResult.tasks.length must beGreaterThan(0)
    }
    
    "Generate tasks for voting for soluions" in {
      val u = User(profile = Profile(publicProfile = PublicProfile(level = 10)))
      val dailyResult = u.getTasksForTomorrow

      val t = dailyResult.tasks.find(_.taskType == TaskType.VoteQuestSolutions)
      t must beSome[Task]
      t.get.currentCount must beEqualTo(0)
      t.get.requiredCount must beEqualTo(17) // 90% from 19
    }
    
    "Do not Generate tasks SubmitQuestResult for low level users" in {
      val u = User(profile = Profile(publicProfile = PublicProfile(level = 1)))
      val dailyResult = u.getTasksForTomorrow

      val t = dailyResult.tasks.find(_.taskType == TaskType.SubmitQuestResult)
      t must beNone
    }
    
    "Generate tasks SubmitQuestResult for low level users" in {
      val u = User(profile = Profile(publicProfile = PublicProfile(level = 13)))
      val u2 = User(profile = u.profile.copy(rights = u.calculateRights))
      
      val dailyResult = u2.getTasksForTomorrow
      
      u2.canSolveQuestToday must beEqualTo(true)

      val t = dailyResult.tasks.find(_.taskType == TaskType.SubmitQuestResult)
      t must beSome[Task]
      t.get.requiredCount must beEqualTo(1)
    }

  }

}

