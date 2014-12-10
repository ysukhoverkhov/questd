package logic.user.util

import logic.constants._
import models.domain._
import logic.UserLogic
import play.Logger
import controllers.domain.app.quest._

trait QuestSelectUserLogic { this: UserLogic =>

  import scala.language.implicitConversions

  def getRandomQuest: Option[Quest] = {
    val algorithms = List(
      () => getQuestsWithSuperAlgorithm,
      () => getQuestsWithMyTags.getOrElse(List().iterator),
      () => getAnyQuests.getOrElse(List().iterator),
      () => getAnyQuestsIgnoringLevels.getOrElse(List().iterator))

    {
      algorithms.foldLeft[Option[Quest]](None)((run, fun) => {
        if (run == None) {
          selectQuest(fun(), List(questIdsToExclude()))
        } else {
          run
        }
      })
    }
  }

  private def questIdsToExclude() = {
    user.timeLine.map(_.objectId)
  }

  private def getQuestsWithSuperAlgorithm = {

    Logger.trace("getQuestsWithSuperAlgorithm")

    val algorithms = List(
      () => getTutorialQuests,
      () => getStartingQuests,
      () => getDefaultQuests)

    selectFromChain(algorithms, default = List().iterator)
  }

  private[user] def getTutorialQuests: Option[Iterator[Quest]] = {
    Logger.trace("getTutorialQuests returns None since does not implemented")
    None
  }

  private[user] def getStartingQuests: Option[Iterator[Quest]] = {
    Logger.trace("getStartingQuests")

    if (user.profile.publicProfile.level > api.config(api.ConfigParams.QuestProbabilityLevelsToGiveStartingQuests).toInt) {
      Logger.trace("  returns None because of high level")
      None
    } else {

      val algorithms = List(
        (api.config(api.ConfigParams.QuestProbabilityStartingVIPQuests).toDouble, () => getVIPQuests),
        (1.00, () => getQuestsWithMyTags) // 1.00 - Last one in the list is 1 to ensure solution will be selected.
        )

      selectNonEmptyIteratorFromRandomAlgorithm(algorithms, dice = rand.nextDouble)
    }
  }

  private[user] def getDefaultQuests: Option[Iterator[Quest]] = {
    Logger.trace("getDefaultQuests")

    val algorithms = List(
      (api.config(api.ConfigParams.QuestProbabilityFriends).toDouble, () => getFriendsQuests),
      (api.config(api.ConfigParams.QuestProbabilityFollowing).toDouble, () => getFollowingQuests),
      (api.config(api.ConfigParams.QuestProbabilityLiked).toDouble, () => getLikedQuests),
      (api.config(api.ConfigParams.QuestProbabilityStar).toDouble, () => getVIPQuests),
      (1.00, () => getQuestsWithMyTags) // 1.00 - Last one in the list is 1 to ensure quest will be selected.
      )

    selectNonEmptyIteratorFromRandomAlgorithm(algorithms, dice = rand.nextDouble)
  }

  private[user] def getFriendsQuests = {
    Logger.trace("  Returning quest from friends")
    Some(api.getFriendsQuests(GetFriendsQuestsRequest(
      user,
      QuestStatus.InRotation,
      levels)).body.get.quests)
  }

  private[user] def getFollowingQuests = {
    Logger.trace("  Returning quest from Following")
    Some(api.getFollowingQuests(GetFollowingQuestsRequest(
      user,
      QuestStatus.InRotation,
      levels)).body.get.quests)
  }

  private[user] def getLikedQuests = {
    Logger.trace("  Returning quests we liked recently")

    Some(api.getLikedQuests(GetLikedQuestsRequest(
      user,
      QuestStatus.InRotation,
      levels)).body.get.quests)
  }

  private[user] def getVIPQuests = {
    Logger.trace("  Returning VIP quests")

    val themeIds = selectRandomThemes(NumberOfFavoriteThemesForVIPQuests)
    Logger.trace("    Selected themes of vip's quests: " + themeIds.mkString(", "))

    Some(api.getVIPQuests(GetVIPQuestsRequest(
      user,
      QuestStatus.InRotation,
      levels)).body.get.quests)
  }

  private[user] def getQuestsWithMyTags = {
    Logger.trace("  Returning quests with my tags")

    val themeIds = selectRandomThemes(NumberOfFavoriteThemesForOtherQuests)
    Logger.trace("    Selected themes of other quests: " + themeIds.mkString(", "))

    Some(api.getAllQuests(GetAllQuestsRequest(
      user,
      QuestStatus.InRotation,
      levels)).body.get.quests)
  }

  private[user] def getAnyQuests = {
    Logger.trace("  Returning from any quests (but respecting levels)")

    Some(api.getAllQuests(GetAllQuestsRequest(
      user,
      QuestStatus.InRotation,
      levels)).body.get.quests)
  }

  private[user] def getAnyQuestsIgnoringLevels = {
    Logger.trace("  Returning from any quests ignoring levels")

    Some(api.getAllQuests(GetAllQuestsRequest(
      user,
      QuestStatus.InRotation,
      None)).body.get.quests)
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
