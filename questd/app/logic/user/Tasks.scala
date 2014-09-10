package logic.user

import java.util.Date
import org.joda.time.DateTime
import com.github.nscala_time.time.Imports._
import play.Logger
import logic._
import logic.constants._
import logic.functions._
import controllers.domain.app.protocol.ProfileModificationResult._
import models.domain._
import models.domain.base._
import models.domain.ContentType._
import controllers.domain.admin._
import controllers.domain._

trait Tasks { this: UserLogic =>

  /**
   * Cooldown for reseting tasks. Should be reset in nearest 5am at user's time.
   */
  def getResetTasksTimeout = getNextFlipHourDate

  /**
   * List of tasks to give user for next day.
   */
  def getTasksForTomorrow = {
    val taskGenerationAlgorithms = getTaskGenerationAlgorithms
    val reward = getTasksReward

    val tasks = TaskType.values.foldLeft(List[Task]())((c, v) => taskGenerationAlgorithms(v)(user) match {
      case Some(t) => t :: c
      case None => c
    })

    DailyTasks(tasks = tasks, reward = reward)
  }

  /**
   * Calculates reward for today's tasks.
   */
  private def getTasksReward = Assets(0, 0, RatingForCompletingDailyTasks)

  /**
   * Returns list of algorithms for generating all tasks.
   */
  private def getTaskGenerationAlgorithms: Map[TaskType.Value, (User) => Option[Task]] = {

    Map(TaskType.VoteQuestSolutions -> getVoteQuestSolutionsTask,
      TaskType.SubmitQuestResult -> getSubmitQuestResultTask,
      TaskType.AddToShortList -> getAddToShortListTask,
      TaskType.VoteQuestProposals -> getVoteQuestProposalsTask,
      TaskType.SubmitQuestProposal -> getSubmitQuestProposalTask,
      TaskType.VoteReviews -> getVoteReviewsTask,
      TaskType.SubmitReviewsForResults -> getSubmitReviewsForResultsTask,
      TaskType.SubmitReviewsForProposals -> getSubmitReviewsForProposalsTask,
      TaskType.GiveRewards -> getGiveRewardsTask,
      TaskType.LookThroughWinnersOfMyQuests -> getLookThroughWinnersOfMyQuestsTask,
      TaskType.LookThroughFriendshipProposals -> getReviewFriendshipRequestsTask,
      TaskType.Client -> getClientTask)
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
  private def getVoteQuestSolutionsTask(user: User) = ifHasRightTo(Functionality.VoteQuestSolutions) {
    def calculateCount = {
      val share = api.config(api.ConfigParams.SolutionVoteTaskShare).toDouble
      Math.round(Math.floor(rewardedSolutionVotesPerLevel(user.profile.publicProfile.level) * share).toFloat)
    }
    
    Some(Task(
      taskType = TaskType.VoteQuestSolutions,
      description = "",
      requiredCount = calculateCount))
  }

  /**
   * Algorithm for generating task for submitting quest.
   */
  private def getSubmitQuestResultTask(user: User) = ifHasRightTo(Functionality.SubmitPhotoResults) {
    if (canSolveQuestToday)
      Some(Task(
        taskType = TaskType.SubmitQuestResult,
        description = "",
        requiredCount = 1))
    else
      None
  }

  /**
   * Algorithm for generating tasks for shortlist.
   */
  private def getAddToShortListTask(user: User) = ifHasRightTo(Functionality.AddToShortList) {
    val prob = api.config(api.ConfigParams.AddToShortlistTaskProbability).toDouble
    if (rand.nextDouble < prob)
      Some(Task(
        taskType = TaskType.AddToShortList,
        description = "",
        requiredCount = 1))
    else
      None
  }

  /**
   * Algorithm for creating task for votes for proposals.
   */
  private def getVoteQuestProposalsTask(user: User) = ifHasRightTo(Functionality.VoteQuestProposals) {
    def calculateCount = {
      val share = api.config(api.ConfigParams.QuestVoteTaskShare).toDouble
      Math.round(Math.floor(rewardedProposalVotesPerLevel(user.profile.publicProfile.level) * share).toFloat)
    }

    Some(Task(
      taskType = TaskType.VoteQuestProposals,
      description = "",
      requiredCount = calculateCount))
  }

  /**
   * Algorithm for generating task for submitting quest proposal.
   */
  private def getSubmitQuestProposalTask(user: User) = ifHasRightTo(Functionality.SubmitPhotoQuests) {
    if (canProposeQuestToday)
      Some(Task(
        taskType = TaskType.SubmitQuestProposal,
        description = "",
        requiredCount = 1))
    else
      None
  }

  /**
   * Algorithm for selecting task for voting reviews.
   */
  private def getVoteReviewsTask(user: User) = ifHasRightTo(Functionality.VoteReviews) {
    //    Some(Task(
    //      taskType = TaskType.VoteReviews,
    //      description = "",
    //      requiredCount = 10))
    None
  }

  /**
   * Algorithm for generating tasks for submiting reviews for solutions.
   */
  private def getSubmitReviewsForResultsTask(user: User) = ifHasRightTo(Functionality.SubmitReviewsForResults) {
    //    Some(Task(
    //      taskType = TaskType.SubmitReviewsForResults,
    //      description = "",
    //      requiredCount = 10))
    None
  }

  /**
   * Algorithm for generating tasks for submiting reviews for proposals.
   */
  private def getSubmitReviewsForProposalsTask(user: User) = ifHasRightTo(Functionality.SubmitReviewsForProposals) {
    //    Some(Task(
    //      taskType = TaskType.SubmitReviewsForProposals,
    //      description = "",
    //      requiredCount = 10))
    None
  }

  /**
   * Algorithm for generating task about giving reward.
   */
  private def getGiveRewardsTask(user: User) = ifHasRightTo(Functionality.GiveRewards) {
    // implement me.  - 20% chance each day.
    //    Some(Task(
    //      taskType = TaskType.GiveRewards,
    //      description = "",
    //      requiredCount = 10))
    None
  }

  private def getLookThroughWinnersOfMyQuestsTask(user: User) = ifHasRightTo(Functionality.SubmitPhotoQuests) {
    //    Some(Task(
    //      taskType = TaskType.LookThroughWinnersOfMyQuests,
    //      description = "",
    //      requiredCount = 10))
    None
  }

  /**
   * Algorithm for generating tasks to review friendship request.
   */
  private def getReviewFriendshipRequestsTask(user: User): Option[Task] = {
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
  private def getClientTask(user: User) = None

}