package logic.user.util

import logic.constants._
import models.domain._
import logic.UserLogic
import play.Logger
import controllers.domain.app.quest._

trait QuestSelectUserLogic { this: UserLogic =>

  import scala.language.implicitConversions

  object QuestGetReason extends Enumeration {
    type QuestGetReason = QuestGetReason.Value
    val ForVoting, ForSolving = Value
  }
  import QuestGetReason._

  // TODO: remove reason here.
  def getRandomQuest(reason: QuestGetReason): Option[Quest] = {
    val algorithms = List(
      () => getQuestsWithSuperAlgorithm(reason),
      () => getOtherQuests(reason).getOrElse(List().iterator),
      () => getAllQuests(reason).getOrElse(List().iterator))

    {
      algorithms.foldLeft[Option[Quest]](None)((run, fun) => {
        if (run == None) {
          selectQuest(fun(), questIdsToExclude(reason))
        } else {
          run
        }
      })
    }
  }

  private def questIdsToExclude(reason: QuestGetReason) = {
    reason match {
      case ForSolving => user.history.solvedQuestIds
      case ForVoting => user.history.votedQuestProposalIds
    }
  }

  def getQuestsWithSuperAlgorithm(reason: QuestGetReason) = {
    val algs = List(
      () => getTutorialQuests(reason),
      () => getStartingQuests(reason),
      () => getDefaultQuests(reason))

    selectFromChain(algs, default = List().iterator)
  }

  private[user] def getTutorialQuests(reason: QuestGetReason): Option[Iterator[Quest]] = {
    Logger.trace("getTutorialQuests")
    None
  }

  private[user] def getStartingQuests(reason: QuestGetReason): Option[Iterator[Quest]] = {
    Logger.trace("getStartingQuests")

    if (user.profile.publicProfile.level > api.config(api.ConfigParams.QuestProbabilityLevelsToGiveStartingQuests).toInt) {
      None
    } else {

      val algs = List(
        (api.config(api.ConfigParams.QuestProbabilityStartingVIPQuests).toDouble, () => getVIPQuests(reason)),
        (1.00, () => getOtherQuests(reason)) // 1.00 - Last one in the list is 1 to ensure solution will be selected.
        )

      selectNonEmptyIteratorFromRandomAlgorithm(algs, dice = rand.nextDouble)

    }
  }

  private[user] def getDefaultQuests(reason: QuestGetReason): Option[Iterator[Quest]] = {
    Logger.trace("getDefaultQuests")

    val algs = List(
      (api.config(api.ConfigParams.QuestProbabilityFriends).toDouble, () => getFriendsQuests(reason)),
      (api.config(api.ConfigParams.QuestProbabilityFollowing).toDouble, () => getFollowingQuests(reason)),
      (api.config(api.ConfigParams.QuestProbabilityLiked).toDouble, () => getLikedQuests(reason)),
      (api.config(api.ConfigParams.QuestProbabilityStar).toDouble, () => getVIPQuests(reason)),
      (1.00, () => getOtherQuests(reason)) // 1.00 - Last one in the list is 1 to ensure quest will be selected.
      )

    selectNonEmptyIteratorFromRandomAlgorithm(algs, dice = rand.nextDouble)
  }

  private[user] def getFriendsQuests(reason: QuestGetReason) = {
    Logger.trace("  Returning quest from friends")
    Some(api.getFriendsQuests(GetFriendsQuestsRequest(
      user,
      reason,
      levels(reason))).body.get.quests)
  }

  private[user] def getFollowingQuests(reason: QuestGetReason) = {
    Logger.trace("  Returning quest from Following")
    Some(api.getFollowingQuests(GetFollowingQuestsRequest(
      user,
      reason,
      levels(reason))).body.get.quests)
  }

  private[user] def getLikedQuests(reason: QuestGetReason) = {
    Logger.trace("  Returning quests we liked recently")

    // If we already liked the quest we are unable to like it once again anymore.
    if (reason == QuestGetReason.ForVoting)
      None
    else
      Some(api.getLikedQuests(GetLikedQuestsRequest(
        user,
        reason,
        levels(reason))).body.get.quests)
  }

  private[user] def getVIPQuests(reason: QuestGetReason) = {
    Logger.trace("  Returning VIP quests")

    val themeIds = selectRandomThemes(NumberOfFavoriteThemesForVIPQuests)
    Logger.trace("    Selected themes of vip's quests: " + themeIds.mkString(", "))

    Some(api.getVIPQuests(GetVIPQuestsRequest(
      user,
      reason,
      levels(reason),
      themeIds)).body.get.quests)
  }

  private[user] def getOtherQuests(reason: QuestGetReason) = {
    Logger.trace("  Returning from all quests with favorite themes")

    val themeIds = selectRandomThemes(NumberOfFavoriteThemesForOtherQuests)
    Logger.trace("    Selected themes of other quests: " + themeIds.mkString(", "))

    Some(api.getAllQuests(GetAllQuestsRequest(
      user,
      reason,
      levels(reason),
      themeIds)).body.get.quests)
  }

  private[user] def getAllQuests(reason: QuestGetReason) = {
    Logger.trace("  Returning from all quests")

    Some(api.getAllQuests(GetAllQuestsRequest(
      user,
      reason,
      levels(reason))).body.get.quests)
  }

  /**
   * Tells what level we should give quests based on reason of getting quest.
   */
  private def levels(reason: QuestGetReason): Option[(Int, Int)] = {
    reason match {
      case ForSolving => Some((user.profile.publicProfile.level - QuestForSolveLevelToleranceDown, user.profile.publicProfile.level + QuestForSolveLevelToleranceUp))
      case ForVoting => None
    }
  }

  implicit private def reasonToStatus(reason: QuestGetReason): QuestStatus.Value = {
    reason match {
      case ForSolving => QuestStatus.InRotation
      case ForVoting => QuestStatus.OnVoting
    }
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
