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

    config.apply(api.ConfigParams.SolutionVoteTaskCountMean) returns "3"
    config.apply(api.ConfigParams.SolutionVoteTaskCountDeviation) returns "1"
    config.apply(api.ConfigParams.CreateSolutionTaskProbability) returns "0.5"
    config.apply(api.ConfigParams.AddToFollowingTaskProbability) returns "0.3"
    config.apply(api.ConfigParams.QuestVoteTaskCountMean) returns "3"
    config.apply(api.ConfigParams.QuestVoteTaskCountDeviation) returns "1"
    config.apply(api.ConfigParams.CreateQuestTaskProbability) returns "0.3"

    config
  }

  "Tasks Logic" should {

    "Generate DailyTasks on request" in {
      api.config returns createStubConfig

      val u = User()
      val dailyResult = u.getTasksForTomorrow

      dailyResult.tasks.length must beGreaterThan(0)
    }

    "Generate tasks for voting for solutions" in {
      api.config returns createStubConfig
      rand.nextGaussian(any, any) returns 3

      val u = User(profile = Profile(publicProfile = PublicProfile(level = 10)))
      val dailyResult = u.getTasksForTomorrow

      val t = dailyResult.tasks.find(_.taskType == TaskType.VoteSolutions)
      t must beSome[Task]
      t.get.currentCount must beEqualTo(0)
      t.get.requiredCount must be_>=(0)
    }

    "Do not Generate tasks CreateSolution for low level users" in {
      api.config returns createStubConfig

      val u = User(profile = Profile(publicProfile = PublicProfile(level = 1)))
      val dailyResult = u.getTasksForTomorrow

      val t = dailyResult.tasks.find(_.taskType == TaskType.CreateSolution)
      t must beNone
    }

    "Generate tasks CreateSolution" in {
      api.config returns createStubConfig
      rand.nextDouble() returns 0.1

      val u = createUser(3)
      val dailyResult = u.getTasksForTomorrow

      u.canSolveQuestToday must beEqualTo(true)

      val t = dailyResult.tasks.find(_.taskType == TaskType.CreateSolution)
      t must beSome[Task]
      t.get.requiredCount must beEqualTo(1)
    }

    "Generate tasks AddToFollowing" in {
      api.config returns createStubConfig
      rand.nextDouble returns 0.2

      val u = createUser(8)
      val dailyResult = u.getTasksForTomorrow

      u.canSolveQuestToday must beEqualTo(true)

      val t = dailyResult.tasks.find(_.taskType == TaskType.AddToFollowing)
      t must beSome[Task]
      t.get.requiredCount must beEqualTo(1)
    }

    "Do not generate tasks AddToFollowing" in {
      api.config returns createStubConfig
      rand.nextDouble returns 0.5

      val u = createUser(8)
      val dailyResult = u.getTasksForTomorrow

      u.canSolveQuestToday must beEqualTo(true)

      val t = dailyResult.tasks.find(_.taskType == TaskType.AddToFollowing)
      t must beNone
    }

    "Generate tasks for voting for quests" in {
      api.config returns createStubConfig
      rand.nextGaussian(any, any) returns 3

      val u = User(profile = Profile(publicProfile = PublicProfile(level = 10)))
      val dailyResult = u.getTasksForTomorrow

      val t = dailyResult.tasks.find(_.taskType == TaskType.VoteQuests)
      t must beSome[Task]
      t.get.requiredCount must beEqualTo(3)
    }

    "Generate tasks for creating quests" in {
      api.config returns createStubConfig
      rand.nextDouble returns 0.1

      val u = createUser(12)
      val dailyResult = u.getTasksForTomorrow

      u.canSolveQuestToday must beEqualTo(true)

      val t = dailyResult.tasks.find(_.taskType == TaskType.CreateQuest)
      t must beSome[Task]
      t.get.requiredCount must beEqualTo(1)
    }

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


