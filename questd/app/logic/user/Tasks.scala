package logic.user

import logic._
import logic.constants._
import logic.functions._
import models.domain._

trait Tasks { this: UserLogic =>

  /**
   * Cooldown for reseting tasks. Should be reset in nearest 5am at user's time.
   */
  def getResetTasksTimeout = nextFlipHourDate

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
      t.copy(reward = allTasksCoinsReward / tasks.length * rand.nextGaussian(mean = 1, dev = DailyTasksRatingDeviation))
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

    Map(TaskType.LikeSolutions -> createLikeSolutionsTask,
      TaskType.CreateSolution -> createCreateSolutionTask,
      TaskType.AddToFollowing -> createAddToFollowingTask,
      TaskType.LikeQuests -> createLikeQuestsTask,
      TaskType.CreateQuest -> createCreateQuestTask,
      TaskType.VoteReviews -> createVoteReviewsTask,
      TaskType.SubmitReviewsForResults -> createSubmitReviewsForResultsTask,
      TaskType.SubmitReviewsForQuests -> createSubmitReviewsForQuestsTask,
      TaskType.GiveRewards -> createGiveRewardsTask,
      TaskType.LookThroughFriendshipProposals -> createReviewFriendshipRequestsTask,
      TaskType.Client -> createClientTask)
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
  private def createLikeSolutionsTask(user: User) = ifHasRightTo(Functionality.VoteQuestSolutions) {
    {
      val mean = api.config(api.ConfigParams.SolutionVoteTaskCountMean).toDouble
      val dev = api.config(api.ConfigParams.SolutionVoteTaskCountDeviation).toDouble
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
    val taskProbability = api.config(api.ConfigParams.CreateSolutionTaskProbability).toDouble
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
    val prob = api.config(api.ConfigParams.AddToFollowingTaskProbability).toDouble
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
      val mean = api.config(api.ConfigParams.QuestVoteTaskCountMean).toDouble
      val dev = api.config(api.ConfigParams.QuestVoteTaskCountDeviation).toDouble
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
    val taskProbability = api.config(api.ConfigParams.CreateQuestTaskProbability).toDouble
    if (canProposeQuestToday && rand.nextDouble() < taskProbability)
      Some(Task(
        taskType = TaskType.CreateQuest,
        description = "",
        requiredCount = 1))
    else
      None
  }

  /**
   * Algorithm for selecting task for voting reviews.
   */
  private def createVoteReviewsTask(user: User) = ifHasRightTo(Functionality.VoteReviews) {
    //    Some(Task(
    //      taskType = TaskType.VoteReviews,
    //      description = "",
    //      requiredCount = 10))
    None
  }

  /**
   * Algorithm for generating tasks for submiting reviews for solutions.
   */
  private def createSubmitReviewsForResultsTask(user: User) = ifHasRightTo(Functionality.SubmitReviewsForSolutions) {
    //    Some(Task(
    //      taskType = TaskType.SubmitReviewsForResults,
    //      description = "",
    //      requiredCount = 10))
    None
  }

  /**
   * Algorithm for generating tasks for submiting reviews for proposals.
   */
  private def createSubmitReviewsForQuestsTask(user: User) = ifHasRightTo(Functionality.SubmitReviewsForQuests) {
    //    Some(Task(
    //      taskType = TaskType.SubmitReviewsForProposals,
    //      description = "",
    //      requiredCount = 10))
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
