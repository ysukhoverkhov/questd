package logic.user.util

import logic.UserLogic
import logic.constants._
import models.domain._
import play.Logger

trait BattleSelectUserLogic { this: UserLogic =>

  import scala.language.implicitConversions

  def getRandomBattle: Option[Battle] = {
    val algorithms = List(
      () => getBattlesWithSuperAlgorithm,
      () => getBattlesWithMyTags,
      () => getAnyBattles,
      () => getAnyBattlesIgnoringLevels)

    val it = selectFromChain(algorithms).getOrElse(Iterator.empty)
    if (it.hasNext) Some(it.next()) else None

  }

  private def questIdsToExclude() = {
    user.timeLine.map(_.objectId)
  }

  private def questAuthorIdsToExclude() = {
    List(user.id)
  }

  private def getBattlesWithSuperAlgorithm: Option[Iterator[Battle]] = {
    Logger.trace("getBattlesWithSuperAlgorithm")

    val algorithms = List(
      () => getTutorialBattles,
      () => getStartingBattles,
      () => getDefaultBattles)

    selectFromChain(algorithms)
  }

  private[user] def getTutorialBattles: Option[Iterator[Battle]] = {
    Logger.trace("getTutorialBattles returns None since does not implemented")
    None
  }

  private[user] def getStartingBattles: Option[Iterator[Battle]] = {
    Logger.trace("getStartingBattles")

    if (user.profile.publicProfile.level > api.config(api.ConfigParams.QuestProbabilityLevelsToGiveStartingQuests).toInt) {
      Logger.trace("  returns None because of high level")
      None
    } else {

      val algorithms = List(
        (api.config(api.ConfigParams.QuestProbabilityStartingVIPQuests).toDouble, () => getVIPBattles),
        (1.00, () => getBattlesWithMyTags) // 1.00 - Last one in the list is 1 to ensure solution will be selected.
        )

      selectNonEmptyIteratorFromRandomAlgorithm(algorithms, dice = rand.nextDouble)
    }
  }

  private[user] def getDefaultBattles: Option[Iterator[Battle]] = {
    Logger.trace("getDefaultBattle")

    val algorithms = List(
      (api.config(api.ConfigParams.QuestProbabilityFriends).toDouble, () => getFriendsBattles),
      (api.config(api.ConfigParams.QuestProbabilityFollowing).toDouble, () => getFollowingBattles),
      (api.config(api.ConfigParams.QuestProbabilityLiked).toDouble, () => getLikedBattles),
      (api.config(api.ConfigParams.QuestProbabilityStar).toDouble, () => getVIPBattles),
      (1.00, () => getBattlesWithMyTags) // 1.00 - Last one in the list is 1 to ensure quest will be selected.
      )

    selectNonEmptyIteratorFromRandomAlgorithm(algorithms, dice = rand.nextDouble)
  }

  private[user] def getFriendsBattles = {
    Logger.trace("  Returning Battle from friends")
    // TODO: implement me.
//    checkNotEmptyIterator(Some(api.getFriendsBattles(GetFriendsBattlesRequest(
//      user,
//      QuestStatus.InRotation,
//      levels)).body.get.quests))
    None
  }

  private[user] def getFollowingBattles = {
    Logger.trace("  Returning Battle from Following")
    // TODO: imeplemnt me.
//    checkNotEmptyIterator(Some(api.getFollowingQuests(GetFollowingQuestsRequest(
//      user,
//      QuestStatus.InRotation,
//      levels)).body.get.quests))
    None
  }

  private[user] def getLikedBattles = {
    Logger.trace("  Returning Battle we liked recently")

//    checkNotEmptyIterator(Some(api.getLikedQuests(GetLikedQuestsRequest(
//      user,
//      QuestStatus.InRotation,
//      levels)).body.get.quests))
    None
  }

  private[user] def getVIPBattles = {
    Logger.trace("  Returning VIP Battles")
// TODO: implement me.
//    val themeIds = selectRandomThemes(NumberOfFavoriteThemesForVIPQuests)
//    Logger.trace("    Selected themes of vip's quests: " + themeIds.mkString(", "))
//
//    checkNotEmptyIterator(Some(api.getVIPQuests(GetVIPQuestsRequest(
//      user,
//      QuestStatus.InRotation,
//      levels)).body.get.quests))
    None
  }

  private[user] def getBattlesWithMyTags = {
    Logger.trace("  Returning Battles with my tags")
// TODO: implement me.
//    val themeIds = selectRandomThemes(NumberOfFavoriteThemesForOtherQuests)
//    Logger.trace("    Selected themes of other quests: " + themeIds.mkString(", "))
//
//    checkNotEmptyIterator(Some(api.getAllQuests(GetAllQuestsRequest(
//      user,
//      QuestStatus.InRotation,
//      levels)).body.get.quests))
    None
  }

  private[user] def getAnyBattles = {
    Logger.trace("  Returning from any Battle (but respecting levels)")
    // TODO: implement me.
//    checkNotEmptyIterator(Some(api.getAllQuests(GetAllQuestsRequest(
//      user,
//      QuestStatus.InRotation,
//      levels)).body.get.quests))
    None
  }

  private[user] def getAnyBattlesIgnoringLevels = {
    // TODO: implement me.
//    Logger.trace("  Returning from any quests ignoring levels")
//
//    checkNotEmptyIterator(Some(api.getAllQuests(GetAllQuestsRequest(
//      user,
//      QuestStatus.InRotation,
//      None)).body.get.quests))
    None
  }

  /**
   * Tells what level we should give quests.
   */
  private def levels: Option[(Int, Int)] = {
    Some((
      user.profile.publicProfile.level - TimeLineContentLevelSigma,
      user.profile.publicProfile.level + TimeLineContentLevelSigma))
  }

  // FIX: change it to tags when they will be ready.
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
