package logic.user.util

import logic.constants._
import models.domain._
import logic.UserLogic
import play.Logger
import controllers.domain.app.solution._

trait SolutionSelectUserLogic { this: UserLogic =>

  def getRandomSolution: Option[Solution] = {
    val algorithms = List(
      () => getSolutionsWithSuperAlgorithm,
      () => getOtherSolutions,
      () => getAnySolutions,
      () => getAnySolutionsIgnoringLevels)


    val it = selectFromChain(algorithms).getOrElse(Iterator.empty)
    if (it.hasNext) Some(it.next()) else None
  }

  private def solutionIdsToExclude = {
    user.timeLine.map(_.objectId)
  }

  private def solutionAuthorIdsToExclude = {
    List(user.id)
  }

  private def getSolutionsWithSuperAlgorithm: Option[Iterator[Solution]] = {
    val algorithms = List(
      () => getTutorialSolutions,
      () => getHelpWantedSolutions,
      () => getSolutionsOfOwnQuests,
      () => getStartingSolutions,
      () => getDefaultSolutions)

    selectFromChain(algorithms)
  }

  private[user] def getTutorialSolutions: Option[Iterator[Solution]] = {
    Logger.trace("getTutorialSolutions")
    None
  }

  private[user] def getHelpWantedSolutions: Option[Iterator[Solution]] = {
    Logger.trace("getHelpWantedSolutions")

    if (user.mustVoteSolutions.nonEmpty) {
      Some(api.getHelpWantedSolutions(GetHelpWantedSolutionsRequest(
        user = user,
        idsExclude = solutionIdsToExclude,
        authorsExclude = solutionAuthorIdsToExclude,
        status = List(SolutionStatus.Won, SolutionStatus.Lost))).body.get.solutions)
    } else {
      None
    }
  }

  private[user] def getSolutionsOfOwnQuests: Option[Iterator[Solution]] = {
    Logger.trace("getSolutionsOfOwnQuests")

    val solutions = api.getSolutionsForOwnQuests(GetSolutionsForOwnQuestsRequest(
      user = user,
      idsExclude = solutionIdsToExclude,
      authorsExclude = solutionAuthorIdsToExclude,
      status = List(SolutionStatus.Won, SolutionStatus.Lost))).body.get.solutions

    if (solutions.isEmpty) None else Some(solutions)
  }

  private[user] def getStartingSolutions: Option[Iterator[Solution]] = {
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

  private[user] def getDefaultSolutions: Option[Iterator[Solution]] = {
    Logger.trace("getDefaultSolutions")

    val algorithms = List(
      (api.config(api.ConfigParams.SolutionProbabilityFriends).toDouble, () => getFriendsSolutions),
      (api.config(api.ConfigParams.SolutionProbabilityFollowing).toDouble, () => getFollowingSolutions),
      (api.config(api.ConfigParams.SolutionProbabilityLiked).toDouble, () => getSolutionsForLikedQuests),
      (api.config(api.ConfigParams.SolutionProbabilityStar).toDouble, () => getVIPSolutions),
      (1.00, () => getOtherSolutions) // 1.00 - Last one in the list is 1 to ensure solution will be selected.
      )

    selectNonEmptyIteratorFromRandomAlgorithm(algorithms, dice = rand.nextDouble)
  }

  private[user] def getFriendsSolutions = {
    Logger.trace("  Returning Solutions from friends")
    checkNotEmptyIterator(Some(api.getFriendsSolutions(GetFriendsSolutionsRequest(
      user = user,
      status = List(SolutionStatus.Won, SolutionStatus.Lost),
      idsExclude = solutionIdsToExclude,
      authorsExclude = solutionAuthorIdsToExclude,
      levels = levels)).body.get.solutions))
  }

  private[user] def getFollowingSolutions = {
    Logger.trace("  Returning solutions from Following")
    checkNotEmptyIterator(Some(api.getFollowingSolutions(GetFollowingSolutionsRequest(
      user = user,
      status = List(SolutionStatus.Won, SolutionStatus.Lost),
      idsExclude = solutionIdsToExclude,
      authorsExclude = solutionAuthorIdsToExclude,
      levels = levels)).body.get.solutions))
  }

  private[user] def getSolutionsForLikedQuests = {
    Logger.trace("  Returning solutions for quests we liked recently")
    checkNotEmptyIterator(Some(api.getSolutionsForLikedQuests(GetSolutionsForLikedQuestsRequest(
      user = user,
      status = List(SolutionStatus.Won, SolutionStatus.Lost),
      idsExclude = solutionIdsToExclude,
      authorsExclude = solutionAuthorIdsToExclude,
      levels = levels)).body.get.solutions))
  }

  private[user] def getVIPSolutions = {
    Logger.trace("  Returning VIP's solutions")

    val themeIds = selectRandomThemes(NumberOfFavoriteThemesForVIPSolutions)
    Logger.trace("    Selected themes of VIP's solutions: " + themeIds.mkString(", "))

    checkNotEmptyIterator(Some(api.getVIPSolutions(GetVIPSolutionsRequest(
      user = user,
      status = List(SolutionStatus.Won, SolutionStatus.Lost),
      idsExclude = solutionIdsToExclude,
      authorsExclude = solutionAuthorIdsToExclude,
      levels = levels,
      themeIds = themeIds)).body.get.solutions))
  }

  private[user] def getOtherSolutions = {
    Logger.trace("  Returning from all solutions with favorite themes")

    val themeIds = selectRandomThemes(NumberOfFavoriteThemesForOtherSolutions)
    Logger.trace("    Selected themes of other solutions: " + themeIds.mkString(", "))

    checkNotEmptyIterator(Some(api.getAllSolutions(GetAllSolutionsRequest(
      user = user,
      status = List(SolutionStatus.Won, SolutionStatus.Lost),
      idsExclude = solutionIdsToExclude,
      authorsExclude = solutionAuthorIdsToExclude,
      levels = levels,
      themeIds = themeIds)).body.get.solutions))
  }

  private[user] def getAnySolutions = {
    Logger.trace("  Returning from all solutions (not ignoring levels)")

    checkNotEmptyIterator(Some(api.getAllSolutions(GetAllSolutionsRequest(
      user = user,
      idsExclude = solutionIdsToExclude,
      authorsExclude = solutionAuthorIdsToExclude,
      status = List(SolutionStatus.Won, SolutionStatus.Lost),
      levels = levels)).body.get.solutions))
  }

  private[user] def getAnySolutionsIgnoringLevels = {
    Logger.trace("  Returning from all solutions (ignoring levels)")

    checkNotEmptyIterator(Some(api.getAllSolutions(GetAllSolutionsRequest(
      user,
      idsExclude = solutionIdsToExclude,
      authorsExclude = solutionAuthorIdsToExclude,
      status = List(SolutionStatus.Won, SolutionStatus.Lost),
      levels = None)).body.get.solutions))
  }

  /**
   * Tells what level we should give quests based on reason of getting quest.
   */
  private def levels: Option[(Int, Int)] = {
    Some((
      user.profile.publicProfile.level - TimeLineContentLevelSigma,
      user.profile.publicProfile.level + TimeLineContentLevelSigma))
  }

  // FIX: change it to tags.
  private def selectRandomThemes(count: Int): List[String] = {
//    if (user.history.themesOfSelectedQuests.length > 0) {
//      for (i <- (1 to count).toList) yield {
//        user.history.themesOfSelectedQuests(rand.nextInt(user.history.themesOfSelectedQuests.length))
//      }
//    } else {
      List()
//    }
  }

}

