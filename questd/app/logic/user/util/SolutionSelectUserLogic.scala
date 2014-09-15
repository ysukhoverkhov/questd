package logic.user.util

import logic.constants._
import models.domain._
import logic.UserLogic
import play.Logger
import controllers.domain.app.questsolution._

trait SolutionSelectUserLogic { this: UserLogic =>

  def getRandomSolution: Option[QuestSolution] = {
    List(
      () => getSolutionsWithSuperAlgorithm,
      () => getOtherSolutions.getOrElse(List().iterator),
      () => getAllSolutions.getOrElse(List().iterator)).
      foldLeft[Option[QuestSolution]](None)((run, fun) => {
        if (run == None) {
          selectQuestSolution(fun(), user.history.votedQuestSolutionIds)
        } else {
          run
        }
      })
  }

  def getSolutionsWithSuperAlgorithm: Iterator[QuestSolution] = {
    val algs = List(
      () => getTutorialSolutions,
      () => getHelpWantedSolutions,
      () => getStartingSolutions,
      () => getDefaultSolutions)

      selectFromChain(algs, default = List().iterator)
  }

  private[user] def getTutorialSolutions: Option[Iterator[QuestSolution]] = {
    Logger.trace("getTutorialSolutions")
    None
  }

  private[user] def getHelpWantedSolutions: Option[Iterator[QuestSolution]] = {
    Logger.trace("getHelpWantedSolutions")

    if (user.mustVoteSolutions.nonEmpty) {
      Some(api.getHelpWantedSolutions(GetHelpWantedSolutionsRequest(
        user,
        QuestSolutionStatus.OnVoting)).body.get.solutions)
    } else {
      None
    }
  }

  private[user] def getStartingSolutions: Option[Iterator[QuestSolution]] = {
    Logger.trace("getStartingSolutions")

    if (user.profile.publicProfile.level > api.config(api.ConfigParams.SolutionProbabilityLevelsToGiveStartingSolutions).toInt) {
      None
    } else {

      val algs = List(
        (api.config(api.ConfigParams.SolutionProbabilityStartingVIPSolutions).toDouble, () => getVIPSolutions),
        (1.00, () => getOtherSolutions) // 1.00 - Last one in the list is 1 to ensure solution will be selected.
        )

      selectNonEmptyIteratorFromRandomAlgorithm(algs, dice = rand.nextDouble)
    }
  }

  private[user] def getDefaultSolutions: Option[Iterator[QuestSolution]] = {
    Logger.trace("getDefaultSolutions")

    val algs = List(
      (api.config(api.ConfigParams.SolutionProbabilityFriends).toDouble, () => getFriendsSolutions),
      (api.config(api.ConfigParams.SolutionProbabilityShortlist).toDouble, () => getShortlistSolutions),
      (api.config(api.ConfigParams.SolutionProbabilityLiked).toDouble, () => getSolutionsForLikedQuests),
      (api.config(api.ConfigParams.SolutionProbabilityStar).toDouble, () => getVIPSolutions),
      (1.00, () => getOtherSolutions) // 1.00 - Last one in the list is 1 to ensure solution will be selected.
      )

    selectNonEmptyIteratorFromRandomAlgorithm(algs, dice = rand.nextDouble)
  }

  private[user] def getFriendsSolutions = {
    Logger.trace("  Returning Solutions from friends")
    Some(api.getFriendsSolutions(GetFriendsSolutionsRequest(
      user,
      QuestSolutionStatus.OnVoting,
      levels)).body.get.solutions)
  }

  private[user] def getShortlistSolutions = {
    Logger.trace("  Returning solutions from shortlist")
    Some(api.getShortlistSolutions(GetShortlistSolutionsRequest(
      user,
      QuestSolutionStatus.OnVoting,
      levels)).body.get.solutions)
  }

  private[user] def getSolutionsForLikedQuests = {
    Logger.trace("  Returning solutions for quests we liked recently")
    Some(api.getSolutionsForLikedQuests(GetSolutionsForLikedQuestsRequest(
      user,
      QuestSolutionStatus.OnVoting,
      levels)).body.get.solutions)
  }

  private[user] def getVIPSolutions = {
    Logger.trace("  Returning VIP's solutions")

    val themeIds = selectRandomThemes(NumberOfFavoriteThemesForVIPSolutions)
    Logger.trace("    Selected themes of VIP's solutions: " + themeIds.mkString(", "))

    Some(api.getVIPSolutions(GetVIPSolutionsRequest(
      user,
      QuestSolutionStatus.OnVoting,
      levels,
      themeIds)).body.get.solutions)
  }

  private[user] def getOtherSolutions = {
    Logger.trace("  Returning from all solutions with favorite themes")

    val themeIds = selectRandomThemes(NumberOfFavoriteThemesForOtherSolutions)
    Logger.trace("    Selected themes of other solutions: " + themeIds.mkString(", "))

    Some(api.getAllSolutions(GetAllSolutionsRequest(
      user,
      QuestSolutionStatus.OnVoting,
      levels,
      themeIds)).body.get.solutions)
  }

  private[user] def getAllSolutions = {
    Logger.trace("  Returning from all solutions")

    Some(api.getAllSolutions(GetAllSolutionsRequest(
      user,
      QuestSolutionStatus.OnVoting,
      levels)).body.get.solutions)
  }

  /**
   * Tells what level we should give quests based on reason of getting quest.
   */
  private def levels: Option[(Int, Int)] = {
    Some((
      user.profile.publicProfile.level - SolutionLevelDownTolerance,
      user.profile.publicProfile.level + SolutionLevelUpTolerance))
  }

  private def selectRandomThemes(count: Int): List[String] = {
    if (user.history.themesOfSelectedQuests.length > 0) {
      for (i <- (1 to count).toList) yield {
        user.history.themesOfSelectedQuests(rand.nextInt(user.history.themesOfSelectedQuests.length))
      }
    } else {
      List()
    }
  }

}

