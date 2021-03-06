package logic.user

import java.util.Date

import logic._
import logic.constants._
import logic.functions._
import models.domain.common.Assets
import models.domain.user._
import models.domain.user.friends.FriendshipStatus
import models.domain.user.profile.{DailyTasks, Functionality, Task, TaskType}

trait Tasks { this: UserLogic =>

  /**
   * Cooldown for resetting tasks. Should be reset in nearest 5am at user's time.
   */
  def getResetTasksTimeout = nextFlipHourDate

  def shouldAssignDailyTasks = {

    if (user.schedules.nextDailyTasksAt.after(new Date())) {
      false
    } else if (user.profile.tutorialStates.valuesIterator.foldLeft(true){
      case (false, _) => false
      case (_, v) => v.dailyTasksSuppression
    }) {
      false
    } else {
      true
    }
  }
  /**
   * List of tasks to give user for next day.
   */
  def getTasksForTomorrow = {
    val dailyRatingReward = dailyTasksRatingReward
    val allTasksCoinsReward = dailyTasksCoinsReward

    val tasks = TaskType.values.foldLeft(List[Task]())((c, v) => taskGenerationAlgorithms(v)(user) match {
      case Some(t) => t :: c
      case None => c
    })

    val tasksWithRewards = tasks.map { t =>
      t.copy(reward = allTasksCoinsReward / tasks.length * rand.nextGaussian(mean = 1, dev = DailyTasksCoinsDeviation))
    }

    DailyTasks(tasks = tasksWithRewards, reward = dailyRatingReward)
  }


  /**
   * Calculates total daily salary in coins for all tasks.
   * @return
   */
  def dailyTasksCoinsReward: Assets = {
    Assets(coins = dailyTasksCoinsSalary(user.profile.publicProfile.level))
  }

  /**
   * Calculates reward for today's tasks.
   */
  private def dailyTasksRatingReward = Assets(0, 0, DailyTasksRatingForCompleting)

  /**
   * Returns list of algorithms for generating all tasks.
   */
  private def taskGenerationAlgorithms: Map[TaskType.Value, (User) => Option[Task]] = {

    Map(
      TaskType.LikeSolutions -> createLikeSolutionsTask,
      TaskType.CreateSolution -> createCreateSolutionTask,
      TaskType.AddToFollowing -> createAddToFollowingTask,
      TaskType.LikeQuests -> createLikeQuestsTask,
      TaskType.CreateQuest -> createCreateQuestTask,
      TaskType.ChallengeBattle -> createChallengeBattleTask,
      TaskType.VoteComments -> createVoteReviewsTask,
      TaskType.PostComments -> createPostCommentsTask,
      TaskType.VoteBattle -> createVoteBattlesTask,
      TaskType.GiveRewards -> createGiveRewardsTask,
      TaskType.LookThroughFriendshipProposals -> createReviewFriendshipRequestsTask,
      TaskType.Custom -> createClientTask)
  }

  /**
   * Wrapper what returns None if user as no rights.
   */
  private def ifHasRightTo(f: Functionality.Value)(body: => Option[Task]): Option[Task] = {
    if (calculateRights.unlockedFunctionality.contains(f))
      body
    else
      None
  }

  /**
   * Algorithm for generating task for voting quests.
   */
  private def createLikeSolutionsTask(user: User) = ifHasRightTo(Functionality.VoteSolutions) {
    {
      val mean = api.config(api.DefaultConfigParams.SolutionVoteTaskCountMean).toDouble
      val dev = api.config(api.DefaultConfigParams.SolutionVoteTaskCountDeviation).toDouble
      math.round(rand.nextGaussian(mean, dev)).toInt
    } match {
      case likesCount if likesCount > 0 =>
        Some(Task(
          taskType = TaskType.LikeSolutions,
          description = "",
          requiredCount = likesCount))
      case _ => None
    }
  }

  /**
   * Algorithm for generating task for submitting quest.
   */
  private def createCreateSolutionTask(user: User) = ifHasRightTo(Functionality.SubmitPhotoSolutions) {
    val taskProbability = api.config(api.DefaultConfigParams.CreateSolutionTaskProbability).toDouble
    if (canSolveQuestToday && rand.nextDouble() < taskProbability)
      Some(Task(
        taskType = TaskType.CreateSolution,
        description = "",
        requiredCount = 1))
    else
      None
  }

  /**
   * Algorithm for generating tasks for following.
   */
  private def createAddToFollowingTask(user: User) = ifHasRightTo(Functionality.AddToFollowing) {
    val prob = api.config(api.DefaultConfigParams.AddToFollowingTaskProbability).toDouble
    if (rand.nextDouble < prob)
      Some(Task(
        taskType = TaskType.AddToFollowing,
        description = "",
        requiredCount = 1))
    else
      None
  }

  /**
   * Algorithm for creating task for votes for proposals.
   */
  private def createLikeQuestsTask(user: User) = ifHasRightTo(Functionality.VoteQuests) {
    {
      val mean = api.config(api.DefaultConfigParams.QuestVoteTaskCountMean).toDouble
      val dev = api.config(api.DefaultConfigParams.QuestVoteTaskCountDeviation).toDouble
      math.round(rand.nextGaussian(mean, dev)).toInt
    } match {
      case likesCount if likesCount > 0 =>
        Some(Task(
          taskType = TaskType.LikeQuests,
          description = "",
          requiredCount = likesCount))
      case _ => None
    }
  }

  /**
   * Algorithm for generating task for submitting quest proposal.
   */
  private def createCreateQuestTask(user: User) = ifHasRightTo(Functionality.SubmitPhotoQuests) {
    val taskProbability = api.config(api.DefaultConfigParams.CreateQuestTaskProbability).toDouble

    if (canProposeQuestToday && rand.nextDouble() < taskProbability)
      Some(Task(
        taskType = TaskType.CreateQuest,
        description = "",
        requiredCount = 1))
    else
      None
  }

  /**
   * Create tasks for challenging people.
   */
  private def createChallengeBattleTask(user: User) = ifHasRightTo(Functionality.ChallengeBattles) {
    val taskProbability = api.config(api.DefaultConfigParams.CreateQuestTaskProbability).toDouble

    if (rand.nextDouble() < taskProbability)
      Some(Task(
        taskType = TaskType.ChallengeBattle,
        description = "",
        requiredCount = 1))
    else
      None
  }

  /**
   * Algorithm for selecting task for voting reviews.
   */
  private def createVoteReviewsTask(user: User) = ifHasRightTo(Functionality.VoteComments) {
//        Some(Task(
//          taskType = TaskType.VoteComments,
//          description = "",
//          requiredCount = 10))
    None
  }


  /**
   * Algorithm for generating tasks for submitting reviews for proposals.
   */
  private def createPostCommentsTask(user: User) = ifHasRightTo(Functionality.PostComments) {
    val taskProbability = api.config(api.DefaultConfigParams.WriteCommentTaskProbability).toDouble
    if (rand.nextDouble() < taskProbability)
      Some(Task(
        taskType = TaskType.PostComments,
        description = "",
        requiredCount = 1))
    else
      None
  }

  /**
   * Algorithm for generating task for voting quests.
   */
  private def createVoteBattlesTask(user: User) = ifHasRightTo(Functionality.VoteBattles) {
    val taskProbability = api.config(api.DefaultConfigParams.BattleVoteTaskProbability).toDouble
    if (rand.nextDouble() < taskProbability)
      Some(Task(
        taskType = TaskType.VoteBattle,
        description = "",
        requiredCount = 1))
    else
      None

  }

  /**
   * Algorithm for generating task about giving reward.
   */
  private def createGiveRewardsTask(user: User) = ifHasRightTo(Functionality.GiveRewards) {
    // implement me.  - 20% chance each day.
    //    Some(Task(
    //      taskType = TaskType.GiveRewards,
    //      description = "",
    //      requiredCount = 10))
    None
  }

  /**
   * Algorithm for generating tasks to review friendship request.
   */
  private def createReviewFriendshipRequestsTask(user: User): Option[Task] = {
    if (user.friends.exists(_.status == FriendshipStatus.Invites))
      Some(Task(
        taskType = TaskType.LookThroughFriendshipProposals,
        description = "",
        requiredCount = 1))
    else
      None
  }

  /**
   * Algorithm for generating Client's custom tasks.
   */
  private def createClientTask(user: User) = None

}
