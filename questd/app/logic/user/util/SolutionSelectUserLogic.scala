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

trait SolutionSelectUserLogic { this: UserLogic =>

  def getRandomSolution: Option[QuestSolution] = {
//    List(
//      () => getSolutionsWithSuperAlgorithm,
//      () => getOtherQuests.getOrElse(List().iterator),
//      () => getAllQuests.getOrElse(List().iterator)).
//      foldLeft[Option[QuestSolution]](None)((run, fun) => {
//        if (run == None) {
//          selectQuestSolution(fun(), user.history.votedQuestSolutionIds)
//        } else {
//          run
//        }
//      })
    None
  }
//
//  def getSolutionsWithSuperAlgorithm = {
//    List(
//      () => getTutorialSolutions,
//      () => getStartingSolutions,
//      () => getDefaultSolutions).
//      foldLeft[Option[Iterator[QuestSolution]]](None)((run, fun) => {
//        if (run == None) fun() else run
//      }).
//      getOrElse(List().iterator)
//  }
//
//  private[user] def getTutorialSolutions: Option[Iterator[QuestSolution]] = {
//    Logger.trace("getTutorialSolutions")
//    None
//  }
//
//  private[user] def getStartingSolutions: Option[Iterator[QuestSolution]] = {
//    Logger.trace("getStartingSolutions")
//
//    if (user.profile.publicProfile.level > api.config(api.ConfigParams.QuestProbabilityLevelsToGiveStartingQuests).toInt) {
//      None
//    } else {
//      if (rand.nextDouble < api.config(api.ConfigParams.QuestProbabilityStartingVIPQuests).toDouble) {
//        getVIPSolutions
//      } else {
//        getOtherSolutions
//      }
//    }
//  }
//
//  private[user] def getDefaultSolutions: Option[Iterator[QuestSolution]] = {
//    Logger.trace("getDefaultSolutions")
//
//    val dice = rand.nextDouble
//
//    List(
//      (api.config(api.ConfigParams.QuestProbabilityFriends).toDouble, () => getFriendsSolutions),
//      (api.config(api.ConfigParams.QuestProbabilityShortlist).toDouble, () => getShortlistSolutions),
//      (api.config(api.ConfigParams.QuestProbabilityLiked).toDouble, () => getLikedSolutions),
//      (api.config(api.ConfigParams.QuestProbabilityStar).toDouble, () => getVIPSolutions),
//      (1.00, () => getOtherSolutions) // 1.00 - Last one in the list is 1 to ensure quest will be selected.
//      ).foldLeft[Either[Double, Option[Iterator[Quest]]]](Left(0))((run, fun) => {
//        run match {
//          case Left(p) => {
//            val curProbabiliy = p + fun._1
//            if (curProbabiliy > dice) {
//              Right(fun._2())
//            } else {
//              Left(curProbabiliy)
//            }
//          }
//          case _ => run
//        }
//      }) match {
//        case Right(oi) => oi match {
//          case Some(i) => if (i.hasNext) Some(i) else None
//          case None => None
//        }
//        case Left(_) => {
//          Logger.error("getDefaultQuests - None of quest selector functions were called. Check probabilities.")
//          None
//        }
//      }
//  }
//
//  private[user] def getFriendsSolutions = {
//    Logger.trace("  Returning Solutions from friends")
//    Some(api.getFriendsQuests(GetFriendsQuestsRequest(
//      user,
//      reason,
//      levels(reason))).body.get.quests)
//  }
//
//  private[user] def getShortlistQuests = {
//    Logger.trace("  Returning quest from shortlist")
//    Some(api.getShortlistQuests(GetShortlistQuestsRequest(
//      user,
//      reason,
//      levels(reason))).body.get.quests)
//  }
//
//  private[user] def getLikedQuests = {
//    Logger.trace("  Returning quests we liked recently")
//    Some(api.getLikedQuests(GetLikedQuestsRequest(
//      user,
//      reason,
//      levels(reason))).body.get.quests)
//  }
//
//  private[user] def getVIPQuests = {
//    Logger.trace("  Returning VIP quests")
//
//    val themeIds = selectRandomThemes(numberOfFavoriteThemesForVIPQuests)
//    Logger.trace("    Selected themes of vip's quests: " + themeIds.mkString(", "))
//
//    Some(api.getVIPQuests(GetVIPQuestsRequest(
//      user,
//      reason,
//      levels(reason),
//      themeIds)).body.get.quests)
//  }
//
//  private[user] def getOtherQuests = {
//    Logger.trace("  Returning from all quests with favorite themes")
//
//    val themeIds = selectRandomThemes(numberOfFavoriteThemesForOtherQuests)
//    Logger.trace("    Selected themes of other quests: " + themeIds.mkString(", "))
//
//    Some(api.getAllQuests(GetAllQuestsRequest(
//      reason,
//      levels(reason),
//      themeIds)).body.get.quests)
//  }
//
//  private[user] def getAllQuests = {
//    Logger.trace("  Returning from all quests")
//
//    Some(api.getAllQuests(GetAllQuestsRequest(
//      reason,
//      levels(reason))).body.get.quests)
//  }
//
//  /**
//   * Tells what level we should give quests based on reason of getting quest.
//   */
//  private def levels = {
//    // TODO implement me.
//    reason match {
//      case ForSolving => Some(user.profile.publicProfile.level - questForSolveLevelToleranceDown, user.profile.publicProfile.level + questForSolveLevelToleranceUp) 
//      case ForVoting => None
//    }
//  }
//
//  private def selectRandomThemes(count: Int): List[String] = {
//    if (user.history.themesOfSelectedQuests.length > 0) {
//      for (i <- (1 to count).toList) yield {
//        user.history.themesOfSelectedQuests(rand.nextInt(user.history.themesOfSelectedQuests.length))
//      }
//    } else {
//      List()
//    }
//  }
//
}

