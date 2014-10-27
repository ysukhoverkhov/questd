package logic.user

import logic.BaseLogicSpecs
import models.domain._
import controllers.domain.config._ConfigParams
import models.domain.admin.ConfigSection

class TasksSpecs extends BaseLogicSpecs {

  private def createUser(level: Int) = {
    val u = User(profile = Profile(publicProfile = PublicProfile(level = level)))
    User(profile = u.profile.copy(rights = u.calculateRights))
  }

  /**
   * Creates stub config for our tests.
   */
  private def createStubConfig = {
    api.ConfigParams returns _ConfigParams

    val config = mock[ConfigSection]

    config.apply(api.ConfigParams.SolutionVoteTaskShare) returns "0.9"
    config.apply(api.ConfigParams.AddToFollowingTaskProbability) returns "0.3"
    config.apply(api.ConfigParams.QuestVoteTaskShare) returns "0.9"

    config
  }

  "Tasks Logic" should {

    "Generate DailyTasks on request" in {
      api.config returns createStubConfig

      val u = User()
      val dailyResult = u.getTasksForTomorrow

      dailyResult.tasks.length must beGreaterThan(0)
    }

    "Generate tasks for voting for soluions" in {
      api.config returns createStubConfig

      val u = User(profile = Profile(publicProfile = PublicProfile(level = 10)))
      val dailyResult = u.getTasksForTomorrow

      val t = dailyResult.tasks.find(_.taskType == TaskType.VoteQuestSolutions)
      t must beSome[Task]
      t.get.currentCount must beEqualTo(0)
      t.get.requiredCount must beEqualTo(17) // 90% from 19
    }

    "Do not Generate tasks SubmitQuestResult for low level users" in {
      api.config returns createStubConfig

      val u = User(profile = Profile(publicProfile = PublicProfile(level = 1)))
      val dailyResult = u.getTasksForTomorrow

      val t = dailyResult.tasks.find(_.taskType == TaskType.SubmitQuestResult)
      t must beNone
    }

    // TODO: clean me up.
//    "Generate tasks SubmitQuestResult" in {
//      api.config returns createStubConfig
//
//      val u = createUser(3)
//      val dailyResult = u.getTasksForTomorrow
//
//      u.canSolveQuestToday must beEqualTo(true)
//
//      val t = dailyResult.tasks.find(_.taskType == TaskType.SubmitQuestResult)
//      t must beSome[Task]
//      t.get.requiredCount must beEqualTo(1)
//    }

    // TODO: clean me up.
//    "Generate tasks AddToFollowing" in {
//      api.config returns createStubConfig
//      rand.nextDouble returns 0.2
//
//      val u = createUser(8)
//      val dailyResult = u.getTasksForTomorrow
//
//      u.canSolveQuestToday must beEqualTo(true)
//
//      val t = dailyResult.tasks.find(_.taskType == TaskType.AddToFollowing)
//      t must beSome[Task]
//      t.get.requiredCount must beEqualTo(1)
//
//      there was one(rand).nextDouble
//    }

    // TODO: clean me up.
//    "Do not generate tasks AddToFollowing" in {
//      api.config returns createStubConfig
//      rand.nextDouble returns 0.5
//
//      val u = createUser(8)
//      val dailyResult = u.getTasksForTomorrow
//
//      u.canSolveQuestToday must beEqualTo(true)
//
//      val t = dailyResult.tasks.find(_.taskType == TaskType.AddToFollowing)
//      t must beNone
//
//      there was one(rand).nextDouble
//    }

    "Generate tasks for voting for proposals" in {
      api.config returns createStubConfig

      val u = User(profile = Profile(publicProfile = PublicProfile(level = 10)))
      val dailyResult = u.getTasksForTomorrow

      val t = dailyResult.tasks.find(_.taskType == TaskType.VoteQuests)
      t must beSome[Task]
      t.get.requiredCount must beEqualTo(3) // 90% from 4
    }

    // TODO: clean me up.
//    "Generate tasks for submitting proposals" in {
//      api.config returns createStubConfig
//
//      val u = createUser(12)
//      val dailyResult = u.getTasksForTomorrow
//
//      u.canSolveQuestToday must beEqualTo(true)
//
//      val t = dailyResult.tasks.find(_.taskType == TaskType.SubmitQuestProposal)
//      t must beSome[Task]
//      t.get.requiredCount must beEqualTo(1)
//    }

    // TODO: clean me up.
//    "Generate tasks for reviewing friendship" in {
//      api.config returns createStubConfig
//
//      val u = createUser(12).copy(friends = List(Friendship(friendId = "", status = FriendshipStatus.Invites)))
//      val dailyResult = u.getTasksForTomorrow
//
//      u.canSolveQuestToday must beEqualTo(true)
//
//      val t = dailyResult.tasks.find(_.taskType == TaskType.LookThroughFriendshipProposals)
//      t must beSome[Task]
//      t.get.requiredCount must beEqualTo(1)
//    }

    // TODO: clean me up.
//    "Generate tasks for reviewing friendship if there are no requests" in {
//      api.config returns createStubConfig
//
//      val u = createUser(12)
//      val dailyResult = u.getTasksForTomorrow
//
//      u.canSolveQuestToday must beEqualTo(true)
//
//      val t = dailyResult.tasks.find(_.taskType == TaskType.LookThroughFriendshipProposals)
//      t must beNone
//    }
  }
}


