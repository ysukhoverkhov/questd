package logic.user

import models.domain._

class TasksSpecs extends BaseUserLogicSpecs {

  private def createUser(level: Int) = {
    val u = User(profile = Profile(publicProfile = PublicProfile(level = level)))
    User(profile = u.profile.copy(rights = u.calculateRights))
  }

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

    "Generate tasks SubmitQuestResult" in {

      val u = createUser(3)
      val dailyResult = u.getTasksForTomorrow

      u.canSolveQuestToday must beEqualTo(true)

      val t = dailyResult.tasks.find(_.taskType == TaskType.SubmitQuestResult)
      t must beSome[Task]
      t.get.requiredCount must beEqualTo(1)
    }

    "Generate tasks AddToShortList" in {
      rand.nextDouble returns 0.2

      val u = createUser(8)
      val dailyResult = u.getTasksForTomorrow

      u.canSolveQuestToday must beEqualTo(true)

      val t = dailyResult.tasks.find(_.taskType == TaskType.AddToShortList)
      t must beSome[Task]
      t.get.requiredCount must beEqualTo(1)
      
      there was one(rand).nextDouble
    }

    "Do not generate tasks AddToShortList" in {
      rand.nextDouble returns 0.5

      val u = createUser(8)
      val dailyResult = u.getTasksForTomorrow

      u.canSolveQuestToday must beEqualTo(true)

      val t = dailyResult.tasks.find(_.taskType == TaskType.AddToShortList)
      t must beNone
      
      there was one(rand).nextDouble
    }

    "Generate tasks for voting for proposals" in {

      val u = User(profile = Profile(publicProfile = PublicProfile(level = 10)))
      val dailyResult = u.getTasksForTomorrow

      val t = dailyResult.tasks.find(_.taskType == TaskType.VoteQuestProposals)
      t must beSome[Task]
      t.get.requiredCount must beEqualTo(3) // 90% from 4
    }
    
  }

}

