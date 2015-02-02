package logic.user.util

import controllers.domain.app.battle.{GetVIPBattlesRequest, GetAllBattlesRequest, GetFollowingBattlesRequest, GetFriendsBattlesRequest}
import logic.UserLogic
import logic.constants._
import models.domain._
import play.Logger


trait BattleSelectUserLogic { this: UserLogic =>

  def getRandomBattles(count: Int): List[Battle] = getRandomObjects[Battle](count, (a: List[Battle]) => getRandomBattle(a))

  private def getRandomBattle(implicit selected: List[Battle]): Option[Battle] = {
    val algorithms = List(
      () => getBattlesWithSuperAlgorithm,
      () => getBattlesWithMyTags,
      () => getAnyBattles,
      () => getAnyBattlesIgnoringLevels)

    val it = selectFromChain(algorithms).getOrElse(Iterator.empty)
    if (it.hasNext) Some(it.next()) else None

  }

  private def getBattlesWithSuperAlgorithm(implicit selected: List[Battle]): Option[Iterator[Battle]] = {
    Logger.trace("getBattlesWithSuperAlgorithm")

    val algorithms = List(
      () => getTutorialBattles,
      () => getStartingBattles,
      () => getDefaultBattles)

    selectFromChain(algorithms)
  }

  private[user] def getTutorialBattles(implicit selected: List[Battle]): Option[Iterator[Battle]] = {
    Logger.trace("getTutorialBattles returns None since does not implemented")
    None
  }

  private[user] def getStartingBattles(implicit selected: List[Battle]): Option[Iterator[Battle]] = {
    Logger.trace("getStartingBattles")

    if (user.profile.publicProfile.level > api.config(api.ConfigParams.BattleProbabilityLevelsToGiveStartingBattles).toInt) {
      Logger.trace("  returns None because of high level")
      None
    } else {

      val algorithms = List(
        (api.config(api.ConfigParams.BattleProbabilityStartingVIPBattles).toDouble, () => getVIPBattles),
        (1.00, () => getBattlesWithMyTags) // 1.00 - Last one in the list is 1 to ensure solution will be selected.
        )

      selectNonEmptyIteratorFromRandomAlgorithm(algorithms, dice = rand.nextDouble())
    }
  }

  private[user] def getDefaultBattles(implicit selected: List[Battle]): Option[Iterator[Battle]] = {
    Logger.trace("getDefaultBattle")

    val algorithms = List(
      (api.config(api.ConfigParams.BattleProbabilityFriends).toDouble, () => getFriendsBattles),
      (api.config(api.ConfigParams.BattleProbabilityFollowing).toDouble, () => getFollowingBattles),
      (api.config(api.ConfigParams.BattleProbabilityLiked).toDouble, () => getBattlesForLikedQuests),
      (api.config(api.ConfigParams.BattleProbabilityVIP).toDouble, () => getVIPBattles),
      (1.00, () => getBattlesWithMyTags) // 1.00 - Last one in the list is 1 to ensure quest will be selected.
      )

    selectNonEmptyIteratorFromRandomAlgorithm(algorithms, dice = rand.nextDouble())
  }

  private[user] def getFriendsBattles(implicit selected: List[Battle]) = {
    Logger.trace("  Returning Battle from friends")
    checkNotEmptyIterator(Some(api.getFriendsBattles(GetFriendsBattlesRequest(
      user,
      List(BattleStatus.Fighting, BattleStatus.Resolved),
      idsExclude = battleIdsToExclude,
      authorsExclude = battleParticipantsIdsToExclude,
      levels)).body.get.battles))
  }

  private[user] def getFollowingBattles(implicit selected: List[Battle]) = {
    Logger.trace("  Returning Battle from Following")
    checkNotEmptyIterator(Some(api.getFollowingBattles(GetFollowingBattlesRequest(
      user = user,
      status = List(BattleStatus.Fighting, BattleStatus.Resolved),
      idsExclude = battleIdsToExclude,
      authorsExclude = battleParticipantsIdsToExclude,
      levels)).body.get.battles))
  }

  private[user] def getBattlesForLikedQuests(implicit selected: List[Battle]) = {
    Logger.trace("  Returning Battle we liked recently")
    // TODO: imeplemnt me.
//    checkNotEmptyIterator(Some(api.getLikedQuests(GetLikedQuestsRequest(
//      user,
//      QuestStatus.InRotation,
//      levels)).body.get.quests))
    None
  }

  private[user] def getVIPBattles(implicit selected: List[Battle]) = {
    Logger.trace("  Returning VIP Battles")

    // TODO: TAGS: implement me (perhaps remove tags).
//    val themeIds = selectRandomThemes(NumberOfFavoriteThemesForVIPQuests)
//    Logger.trace("    Selected themes of vip's quests: " + themeIds.mkString(", "))
//
    checkNotEmptyIterator(Some(api.getVIPBattles(GetVIPBattlesRequest(
      user = user,
      status = List(BattleStatus.Fighting, BattleStatus.Resolved),
      idsExclude = battleIdsToExclude,
      authorsExclude = battleParticipantsIdsToExclude,
      levels = levels)).body.get.battles))
  }

  private[user] def getBattlesWithMyTags(implicit selected: List[Battle]) = {
    Logger.trace("  Returning Battles with my tags")
// TODO: TAGS: implement me with tags.
//    val themeIds = selectRandomThemes(NumberOfFavoriteThemesForOtherQuests)
//    Logger.trace("    Selected themes of other quests: " + themeIds.mkString(", "))
//
//    checkNotEmptyIterator(Some(api.getAllQuests(GetAllQuestsRequest(
//      user,
//      QuestStatus.InRotation,
//      levels)).body.get.quests))
    None
  }

  private[user] def getAnyBattles(implicit selected: List[Battle]) = {
    Logger.trace("  Returning from any Battle (but respecting levels)")

    checkNotEmptyIterator(Some(api.getAllBattles(GetAllBattlesRequest(
      user = user,
      idsExclude = battleIdsToExclude,
      authorIdsExclude = battleParticipantsIdsToExclude,
      levels = levels)).body.get.battles))
  }

  private[user] def getAnyBattlesIgnoringLevels(implicit selected: List[Battle]) = {
    Logger.trace("  Returning from any battles ignoring levels")

    checkNotEmptyIterator(Some(api.getAllBattles(GetAllBattlesRequest(
      user = user,
      idsExclude = battleIdsToExclude,
      authorIdsExclude = battleParticipantsIdsToExclude,
      levels = None)).body.get.battles))
  }

  /**
   * Tells what level we should give quests.
   */
  private def levels: Option[(Int, Int)] = {
    Some((
      user.profile.publicProfile.level - TimeLineContentLevelSigma,
      user.profile.publicProfile.level + TimeLineContentLevelSigma))
  }

  private def battleIdsToExclude(implicit selected: List[Battle]) = {
    user.timeLine.map(_.objectId) ::: selected.map(_.id)
  }

  private def battleParticipantsIdsToExclude = {
    List(user.id)
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
