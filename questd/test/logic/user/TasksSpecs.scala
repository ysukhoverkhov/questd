package logic.user

import logic.BaseLogicSpecs
import models.domain.user._
import models.domain.user.friends.{Friendship, FriendshipStatus}
import models.domain.user.profile._
import testhelpers.domainstubs._

class TasksSpecs extends BaseLogicSpecs {

  private def createUser(level: Int) = {
    val u = createUserStub(level = level)
    User(profile = u.profile.copy(rights = u.calculateRights))
  }

  "Tasks Logic" should {

    "Generate DailyTasks on request" in context {
      rand.nextGaussian(any, any) returns 1

      val u = createUserStub(level = 20)
      val dailyResult = u.getTasksForTomorrow

      dailyResult.tasks.length must beGreaterThan(0)
    }

    "Do not generate DailyTasks for users with tasks supressed by tutorial" in context {
      rand.nextGaussian(any, any) returns 1

      val u = createUserStub(level = 1, tutorialState = TutorialState(dailyTasksSuppression = true))
      val dailyResult = u.shouldAssignDailyTasks

      dailyResult must beFalse
    }

    "Generate tasks for voting for solutions" in context {
      rand.nextGaussian(any, any) returns 3

      val u = createUser(10)
      val dailyResult = u.getTasksForTomorrow

      val t = dailyResult.tasks.find(_.taskType == TaskType.LikeSolutions)
      t must beSome[Task]
      t.get.currentCount must beEqualTo(0)
      t.get.requiredCount must be_>=(0)
    }

    "Do not Generate tasks CreateSolution for low level users" in context {
      val u = User(profile = Profile(publicProfile = PublicProfile(level = 1)))
      val dailyResult = u.getTasksForTomorrow

      val t = dailyResult.tasks.find(_.taskType == TaskType.CreateSolution)
      t must beNone
    }

    "Generate tasks CreateSolution" in context {
      rand.nextDouble() returns 0.1

      val u = createUser(3)
      val dailyResult = u.getTasksForTomorrow

      u.canSolveQuestToday must beEqualTo(true)

      val t = dailyResult.tasks.find(_.taskType == TaskType.CreateSolution)
      t must beSome[Task]
      t.get.requiredCount must beEqualTo(1)
    }

    "Generate tasks AddToFollowing" in context {
      rand.nextDouble returns 0.2

      val u = createUser(8)
      val dailyResult = u.getTasksForTomorrow

      u.canSolveQuestToday must beEqualTo(true)

      val t = dailyResult.tasks.find(_.taskType == TaskType.AddToFollowing)
      t must beSome[Task]
      t.get.requiredCount must beEqualTo(1)
    }

    "Do not generate tasks AddToFollowing" in context {
      rand.nextDouble returns 0.5

      val u = createUser(8)
      val dailyResult = u.getTasksForTomorrow

      u.canSolveQuestToday must beEqualTo(true)

      val t = dailyResult.tasks.find(_.taskType == TaskType.AddToFollowing)
      t must beNone
    }

    "Generate tasks for voting for quests" in context {
      rand.nextGaussian(any, any) returns 3

      val u = createUser(10)
      val dailyResult = u.getTasksForTomorrow

      val t = dailyResult.tasks.find(_.taskType == TaskType.LikeQuests)
      t must beSome[Task]
      t.get.requiredCount must beEqualTo(3)
    }

    "Generate tasks for creating quests" in context {
      rand.nextDouble returns 0.1

      val u = createUser(12)
      val dailyResult = u.getTasksForTomorrow

      u.canSolveQuestToday must beEqualTo(true)

      val t = dailyResult.tasks.find(_.taskType == TaskType.CreateQuest)
      t must beSome[Task]
      t.get.requiredCount must beEqualTo(1)
    }

    "Generate tasks for reviewing friendship" in context {
      val u = createUser(12).copy(friends = List(Friendship(friendId = "", status = FriendshipStatus.Invites)))
      val dailyResult = u.getTasksForTomorrow

      u.canSolveQuestToday must beEqualTo(true)

      val t = dailyResult.tasks.find(_.taskType == TaskType.LookThroughFriendshipProposals)
      t must beSome[Task]
      t.get.requiredCount must beEqualTo(1)
    }

    "Generate tasks for reviewing friendship if there are no requests" in context {
      val u = createUser(12)
      val dailyResult = u.getTasksForTomorrow

      val t = dailyResult.tasks.find(_.taskType == TaskType.LookThroughFriendshipProposals)
      t must beNone
    }

    "Generate tasks for challenging battles" in context {
      val u = createUser(12)
      val dailyResult = u.getTasksForTomorrow

      val t = dailyResult.tasks.find(_.taskType == TaskType.ChallengeBattle)
      t must beSome[Task]
      t.get.requiredCount must beEqualTo(1)
    }

    "Generate tasks for video quests" in context {
      val u = createUser(12)
      val dailyResult = u.getTasksForTomorrow

      val t = dailyResult.tasks.find(_.taskType == TaskType.CreateVideoQuest)
      t must beSome[Task]
      t.get.requiredCount must beEqualTo(1)
    }

    "Generate tasks for video solutions" in context {
      val u = createUser(12)
      val dailyResult = u.getTasksForTomorrow

      val t = dailyResult.tasks.find(_.taskType == TaskType.CreateVideoSolution)
      t must beSome[Task]
      t.get.requiredCount must beEqualTo(1)
    }
  }
}

