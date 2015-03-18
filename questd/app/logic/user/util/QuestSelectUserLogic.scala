package logic.user.util

import logic.constants._
import models.domain._
import logic.UserLogic
import play.Logger
import controllers.domain.app.quest._

trait QuestSelectUserLogic { this: UserLogic =>

  def getRandomQuests(count: Int): List[Quest] = getRandomObjects[Quest](count, (a: List[Quest]) => getRandomQuest(a))

  private def getRandomQuest(implicit selected: List[Quest]): Option[Quest] = {
    val algorithms = List(
      () => getQuestsWithSuperAlgorithm,
      () => getQuestsWithMyTags,
      () => getAnyQuests,
      () => getAnyQuestsIgnoringLevels,
      () => getAnyQuestsIgnoringLevelsAndCulture)

    val it = selectFromChain(algorithms).getOrElse(Iterator.empty)
    if (it.hasNext) Some(it.next()) else None
  }

  private def getQuestsWithSuperAlgorithm(implicit selected: List[Quest]): Option[Iterator[Quest]] = {
    Logger.trace("getQuestsWithSuperAlgorithm")

    val algorithms = List(
      () => getTutorialQuests,
      () => getStartingQuests,
      () => getDefaultQuests)

    selectFromChain(algorithms)
  }

  private[user] def getTutorialQuests: Option[Iterator[Quest]] = {
    Logger.trace("getTutorialQuests returns None since does not implemented")
    None
  }

  private[user] def getStartingQuests(implicit selected: List[Quest]): Option[Iterator[Quest]] = {
    Logger.trace("getStartingQuests")

    if (user.profile.publicProfile.level > api.config(api.ConfigParams.QuestProbabilityLevelsToGiveStartingQuests).toInt) {
      Logger.trace("  returns None because of high level")
      None
    } else {

      val algorithms = List(
        (api.config(api.ConfigParams.QuestProbabilityStartingVIPQuests).toDouble, () => getVIPQuests),
        (1.00, () => getQuestsWithMyTags) // 1.00 - Last one in the list is 1 to ensure solution will be selected.
        )

      selectNonEmptyIteratorFromRandomAlgorithm(algorithms, dice = rand.nextDouble())
    }
  }

  private[user] def getDefaultQuests(implicit selected: List[Quest]): Option[Iterator[Quest]] = {
    Logger.trace("getDefaultQuests")

    val algorithms = List(
      (api.config(api.ConfigParams.QuestProbabilityFriends).toDouble, () => getFriendsQuests),
      (api.config(api.ConfigParams.QuestProbabilityFollowing).toDouble, () => getFollowingQuests),
      (api.config(api.ConfigParams.QuestProbabilityVIP).toDouble, () => getVIPQuests),
      (1.00, () => getQuestsWithMyTags) // 1.00 - Last one in the list is 1 to ensure quest will be selected.
      )

    selectNonEmptyIteratorFromRandomAlgorithm(algorithms, dice = rand.nextDouble())
  }

  private[user] def getFriendsQuests(implicit selected: List[Quest]) = {
    Logger.trace("  Returning quest from friends")
    checkNotEmptyIterator(Some(api.getFriendsQuests(GetFriendsQuestsRequest(
      user = user,
      status = QuestStatus.InRotation,
      idsExclude = questIdsToExclude,
      authorsExclude = questAuthorIdsToExclude,
      levels = levels)).body.get.quests))
  }

  private[user] def getFollowingQuests(implicit selected: List[Quest]) = {
    Logger.trace("  Returning quest from Following")
    checkNotEmptyIterator(Some(api.getFollowingQuests(GetFollowingQuestsRequest(
      user = user,
      status = QuestStatus.InRotation,
      levels = levels,
      idsExclude = questIdsToExclude,
      authorsExclude = questAuthorIdsToExclude
    )).body.get.quests))
  }

  private[user] def getVIPQuests(implicit selected: List[Quest]) = {
    Logger.trace("  Returning VIP quests")

    val themeIds = selectRandomThemes(NumberOfFavoriteThemesForVIPQuests)
    Logger.trace("    Selected themes of vip's quests: " + themeIds.mkString(", "))

    checkNotEmptyIterator(Some(api.getVIPQuests(GetVIPQuestsRequest(
      user = user,
      idsExclude = questIdsToExclude,
      authorsExclude = questAuthorIdsToExclude,
      status = QuestStatus.InRotation,
      levels = levels)).body.get.quests))
  }

  private[user] def getQuestsWithMyTags(implicit selected: List[Quest]) = {
    Logger.trace("  Returning quests with my tags")

    val themeIds = selectRandomThemes(NumberOfFavoriteThemesForOtherQuests)
    Logger.trace("    Selected themes of other quests: " + themeIds.mkString(", "))

    checkNotEmptyIterator(Some(api.getAllQuests(GetAllQuestsRequest(
      user = user,
      idsExclude = questIdsToExclude,
      status = QuestStatus.InRotation,
      authorsExclude = questAuthorIdsToExclude,
      levels = levels,
      cultureId = user.demo.cultureId)).body.get.quests))
  }

  private[user] def getAnyQuests(implicit selected: List[Quest]) = {
    Logger.trace("  Returning from any quests (but respecting levels)")

    checkNotEmptyIterator(Some(api.getAllQuests(GetAllQuestsRequest(
      user = user,
      idsExclude = questIdsToExclude,
      authorsExclude = questAuthorIdsToExclude,
      status = QuestStatus.InRotation,
      levels = levels,
      cultureId = user.demo.cultureId)).body.get.quests))
  }

  private[user] def getAnyQuestsIgnoringLevels(implicit selected: List[Quest]) = {
    Logger.trace("  Returning from any quests ignoring levels")

    checkNotEmptyIterator(Some(api.getAllQuests(GetAllQuestsRequest(
      user = user,
      idsExclude = questIdsToExclude,
      authorsExclude = questAuthorIdsToExclude,
      status = QuestStatus.InRotation,
      levels = None,
      cultureId = user.demo.cultureId)).body.get.quests))
  }

  private[user] def getAnyQuestsIgnoringLevelsAndCulture(implicit selected: List[Quest]) = {
    Logger.trace("  Returning from any quests ignoring levels and culture")

    checkNotEmptyIterator(Some(api.getAllQuests(GetAllQuestsRequest(
      user = user,
      idsExclude = questIdsToExclude,
      authorsExclude = questAuthorIdsToExclude,
      status = QuestStatus.InRotation,
      levels = None,
      cultureId = None)).body.get.quests))
  }

  /**
   * Tells what level we should give quests.
   */
  private def levels: Option[(Int, Int)] = {
    Some((
      user.profile.publicProfile.level - TimeLineContentLevelSigma,
      user.profile.publicProfile.level + TimeLineContentLevelSigma))
  }

  private def questIdsToExclude(implicit selected: List[Quest]) = {
    user.timeLine.map(_.objectId) ::: user.stats.votedQuests.keys.toList ::: selected.map(_.id)
  }

  private def questAuthorIdsToExclude = {
    List(user.id)
  }

  // FIX: change it to tags when they will be ready.
  private def selectRandomThemes(count: Int): List[String] = {
//    if (user.history.themesOfSelectedQuests.length > 0) {
//      for (i <- (1 to count).toList) yield {
//        user.history.themesOfSelectedQuests(rand.nextInt(user.history.themesOfSelectedQuests.length))
//      }
//    } else {
      List.empty
//    }
  }

}
