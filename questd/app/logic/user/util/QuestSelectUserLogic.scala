package logic.user.util

import org.joda.time.DateTime
import com.github.nscala_time.time.Imports._
import logic._
import logic.constants._
import logic.functions._
import controllers.domain.app.protocol.ProfileModificationResult._
import models.domain._
import models.domain.base._
import models.domain.ContentType._
import controllers.domain._
import logic.UserLogic
import play.Logger
import controllers.domain.app.user._
import controllers.domain.app.quest._

trait QuestSelectUserLogic { this: UserLogic =>

  import scala.language.implicitConversions

  object QuestGetReason extends Enumeration {
    type QuestGetReason = QuestGetReason.Value
    val ForVoting, ForSolving = Value
  }
  import QuestGetReason._

  def getRandomQuest(reason: QuestGetReason): Option[Quest] = {
    List(
      () => getQuestsWithSuperAlgorithm(reason),
      () => getOtherQuests(reason).getOrElse(List().iterator),
      () => getAllQuests(reason).getOrElse(List().iterator)).
      foldLeft[Option[Quest]](None)((run, fun) => {
        if (run == None) {
          selectQuest(fun(), user.history.solvedQuestIds)
        } else {
          run
        }
      })
  }

  def getQuestsWithSuperAlgorithm(reason: QuestGetReason) = {
    List(
      () => getTutorialQuests(reason),
      () => getStartingQuests(reason),
      () => getDefaultQuests(reason)).
      foldLeft[Option[Iterator[Quest]]](None)((run, fun) => {
        if (run == None) fun() else run
      }).
      getOrElse(List().iterator)
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
      if (rand.nextDouble < api.config(api.ConfigParams.QuestProbabilityStartingVIPQuests).toDouble) {
        getVIPQuests(reason)
      } else {
        getOtherQuests(reason)
      }
    }
  }

  private[user] def getDefaultQuests(reason: QuestGetReason): Option[Iterator[Quest]] = {
    Logger.trace("getDefaultQuests")

    val dice = rand.nextDouble

    List(
      (api.config(api.ConfigParams.QuestProbabilityFriends).toDouble, () => getFriendsQuests(reason)),
      (api.config(api.ConfigParams.QuestProbabilityShortlist).toDouble, () => getShortlistQuests(reason)),
      (api.config(api.ConfigParams.QuestProbabilityLiked).toDouble, () => getLikedQuests(reason)),
      (api.config(api.ConfigParams.QuestProbabilityStar).toDouble, () => getVIPQuests(reason)),
      (1.00, () => getOtherQuests(reason)) // 1.00 - Last one in the list is 1 to ensure quest will be selected.
      ).foldLeft[Either[Double, Option[Iterator[Quest]]]](Left(0))((run, fun) => {
        run match {
          case Left(p) => {
            val curProbabiliy = p + fun._1
            if (curProbabiliy > dice) {
              Right(fun._2())
            } else {
              Left(curProbabiliy)
            }
          }
          case _ => run
        }
      }) match {
        case Right(oi) => oi match {
          case Some(i) => if (i.hasNext) Some(i) else None
          case None => None
        }
        case Left(_) => {
          Logger.error("getDefaultQuests - None of quest selector functions were called. Check probabilities.")
          None
        }
      }
  }

  private[user] def getFriendsQuests(reason: QuestGetReason) = {
    Logger.trace("  Returning quest from friends")
    Some(api.getFriendsQuests(GetFriendsQuestsRequest(
      user,
      reason,
      levelFrom(reason),
      levelTo(reason))).body.get.quests)
  }

  private[user] def getShortlistQuests(reason: QuestGetReason) = {
    Logger.trace("  Returning quest from shortlist")
    Some(api.getShortlistQuests(GetShortlistQuestsRequest(
      user,
      reason,
      levelFrom(reason),
      levelTo(reason))).body.get.quests)
  }

  private[user] def getLikedQuests(reason: QuestGetReason) = {
    Logger.trace("  Returning quests we liked recently")
    Some(api.getLikedQuests(GetLikedQuestsRequest(
      user,
      reason,
      levelFrom(reason),
      levelTo(reason))).body.get.quests)
  }

  private[user] def getVIPQuests(reason: QuestGetReason) = {
    Logger.trace("  Returning VIP quests")

    val themeIds = selectRandomThemes(numberOfFavoriteThemesForVIPQuests)
    Logger.trace("    Selected themes of vip's quests: " + themeIds.mkString(", "))

    Some(api.getVIPQuests(GetVIPQuestsRequest(
      user,
      reason,
      levelFrom(reason),
      levelTo(reason),
      themeIds)).body.get.quests)
  }

  private[user] def getOtherQuests(reason: QuestGetReason) = {
    Logger.trace("  Returning from all quests with favorite themes")

    val themeIds = selectRandomThemes(numberOfFavoriteThemesForOtherQuests)
    Logger.trace("    Selected themes of other quests: " + themeIds.mkString(", "))

    Some(api.getAllQuests(GetAllQuestsRequest(
      reason,
      levelFrom(reason),
      levelTo(reason),
      themeIds)).body.get.quests)
  }

  private[user] def getAllQuests(reason: QuestGetReason) = {
    Logger.trace("  Returning from all quests")

    Some(api.getAllQuests(GetAllQuestsRequest(
      reason,
      levelFrom(reason),
      levelTo(reason))).body.get.quests)
  }

  /**
   * Tells starting from what level we should give quests based on reason of getting quest.
   */
  private def levelFrom(reason: QuestGetReason) = {
    reason match {
      case ForSolving => user.profile.publicProfile.level - questForSolveLevelToleranceDown
      case ForVoting => -1//constants.minQuestLevel TODO:
    }
  }

  /**
   * Tells starting to what level we should give quests based on reason of getting quest.
   */
  private def levelTo(reason: QuestGetReason) = {
    reason match {
      case ForSolving => user.profile.publicProfile.level + questForSolveLevelToleranceUp
      case ForVoting => constants.maxQuestLevel
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
