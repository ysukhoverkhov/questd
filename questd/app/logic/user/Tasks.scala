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
    // TODO: test me.

    val taskGenerationAlgorithms = getTaskGenerationAlgorithms
    val reward = getTasksReward

    val tasks = TaskType.values.foldLeft(List[Task]())((c, v) => taskGenerationAlgorithms(v)(user) match {
      case Some(t) => t :: c
      case None => c
    })

    DailyTasks(tasks = tasks, reward = reward)
  }

  private def getTasksReward = Assets(0, 0, RatingForCompletingDailyTasks)

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
      TaskType.Client -> getClientTask)

  }

  private def ifHasRightTo(f: Functionality.Value)(body: => Option[Task]): Option[Task] = {
    if (calculateRights.unlockedFunctionality.contains(f.toString))
      body
    else
      None
  }

  // TODO: implement me.
  private def getVoteQuestSolutionsTask(user: User) = ifHasRightTo(Functionality.VoteQuestSolutions) {
      Some(Task(
        taskType = TaskType.VoteQuestSolutions,
        description = "",
        requiredCount = 10))
  }

  // TODO: implement me.
  private def getSubmitQuestResultTask(user: User) = ifHasRightTo(Functionality.SubmitPhotoResults) {
    Some(Task(
      taskType = TaskType.SubmitQuestResult,
      description = "",
      requiredCount = 10))
  }

  // TODO: implement me.
  private def getAddToShortListTask(user: User) = ifHasRightTo(Functionality.AddToShortList) {
    Some(Task(
      taskType = TaskType.AddToShortList,
      description = "",
      requiredCount = 10))
  }

  // TODO: implement me.
  private def getVoteQuestProposalsTask(user: User) = ifHasRightTo(Functionality.VoteQuestProposals) {
    Some(Task(
      taskType = TaskType.VoteQuestProposals,
      description = "",
      requiredCount = 10))
  }

  // TODO: implement me.
  private def getSubmitQuestProposalTask(user: User) = ifHasRightTo(Functionality.SubmitPhotoQuests) {
    Some(Task(
      taskType = TaskType.SubmitQuestProposal,
      description = "",
      requiredCount = 10))
  }

  // TODO: implement me.
  private def getVoteReviewsTask(user: User) = ifHasRightTo(Functionality.VoteReviews) {
    Some(Task(
      taskType = TaskType.VoteReviews,
      description = "",
      requiredCount = 10))
  }

  // TODO: implement me.
  private def getSubmitReviewsForResultsTask(user: User) = ifHasRightTo(Functionality.SubmitReviewsForResults) {
    Some(Task(
      taskType = TaskType.SubmitReviewsForResults,
      description = "",
      requiredCount = 10))
  }

  // TODO: implement me.
  private def getSubmitReviewsForProposalsTask(user: User) = ifHasRightTo(Functionality.SubmitReviewsForProposals) {
    Some(Task(
      taskType = TaskType.SubmitReviewsForProposals,
      description = "",
      requiredCount = 10))
  }

  // TODO: implement me.
  private def getGiveRewardsTask(user: User) = ifHasRightTo(Functionality.GiveRewards) {
    Some(Task(
      taskType = TaskType.GiveRewards,
      description = "",
      requiredCount = 10))
  }

  // TODO: implement me.
  private def getLookThroughWinnersOfMyQuestsTask(user: User) = ifHasRightTo(Functionality.SubmitPhotoQuests) {
    Some(Task(
      taskType = TaskType.LookThroughWinnersOfMyQuests,
      description = "",
      requiredCount = 10))
  }

  private def getClientTask(user: User) = {
    None
  }

}
