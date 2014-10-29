package logic.user

import java.util.Date
import logic._
import logic.constants._
import logic.functions._
import controllers.domain.app.protocol.ProfileModificationResult._
import models.domain._
import models.domain.ContentType._

/**
 * All logic related to proposing quests.
 */
trait CreatingQuests { this: UserLogic =>

  /**
   * Is user potentially eligible for proposing quest today.
   */
  def canProposeQuestToday = {
    user.profile.rights.unlockedFunctionality.contains(Functionality.SubmitPhotoQuests) &&
    user.profile.questCreationContext.questCreationCoolDown.before(new Date())
  }

  /**
   * Is user can propose quest of given type.
   */
  def canCreateQuest(questContent: QuestInfoContent) = {
    val content = questContent.media.contentType match {
      case Photo => user.profile.rights.unlockedFunctionality.contains(Functionality.SubmitPhotoQuests)
      case Video => user.profile.rights.unlockedFunctionality.contains(Functionality.SubmitVideoQuests)
    }

    if (!content)
      NotEnoughRights
    else if (!canProposeQuestToday)
      CoolDown
    else if (questContent.description.length > api.config(api.ConfigParams.ProposalMaxDescriptionLength).toInt)
      LimitExceeded
    else if (user.demo.cultureId == None || user.profile.publicProfile.bio.gender == Gender.Unknown)
      IncompleteBio
    else
      OK
  }

  // TODO: clean me up.
    /**
   * Tells cost of next theme purchase
   */
//  def costOfPurchasingQuestProposal = {
//    if (user.profile.questProposalContext.numberOfPurchasedThemes < NumberOfThemesSkipsForCoins) {
//      val c = costToSkipTheme(user.profile.publicProfile.level, user.profile.questProposalContext.numberOfPurchasedThemes)
//      Assets(coins = c)
//    } else {
//      Assets(money = 1)
//    }
//  }

  /**
   * Cost of proposing quest.
   */
//  def costOfTakingQuestTheme = {
//    Assets(coins = costToTakeQuestTheme(user.profile.publicProfile.level))
//  }

  /**
   * Select theme for the user to take.
   */
//  def getRandomThemeForQuestProposal(themesCount: Long): Option[Theme] = {
//
//    /**
//     * Select theme from returned iterator what is not contained in given list.
//     */
//    @tailrec
//    def selectTheme(i: Iterator[Theme], usedThemesIds: List[String]): Option[Theme] = {
//      Logger.trace("In selectTheme")
//      if (i.hasNext) {
//        val t = i.next()
//
//        Logger.trace("Checking used themes to have reviewed theme in: " + t.id + " IN " + usedThemesIds)
//        if (!usedThemesIds.contains(t.id)) {
//          Logger.trace("  Theme selected: " + t.id)
//          Some(t)
//        } else {
//          selectTheme(i, usedThemesIds)
//        }
//      } else {
//        None
//      }
//    }
//
//    /**
//     * Select a theme from global list of themes.
//     */
//    def themeFromGlobal: Option[Theme] = {
//      user.demo.cultureId match {
//        case Some(c) =>
//          val themes = api.getAllThemesForCulture(GetAllThemesForCultureRequest(cultureId = c)).body.get.themes
//          selectTheme(themes, user.profile.questProposalContext.todayReviewedThemeIds)
//        case None =>
//          Logger.error("User with culture set to None requesting culture.")
//          None
//      }
//    }
//
//    val probabilityOfRecentList = {
//      val maxShare = api.config(api.ConfigParams.FavoriteThemesShare).toDouble
//      val minShare = maxShare / 4
//      val ourShare = user.history.selectedThemeIds.length / themesCount.toDouble
//
//      if (minShare > ourShare)
//        0
//      else
//        Math.min(1, (ourShare - minShare) / (maxShare - minShare)) * api.config(api.ConfigParams.FavoriteThemesProbability).toDouble
//    }
//
//    Logger.debug("Probability of recent list " + probabilityOfRecentList)
//
//    if (rand.nextDouble < probabilityOfRecentList) {
//      // Use recent list
//      Logger.debug("Using recent list")
//      val id = user.history.selectedThemeIds(rand.nextInt(user.history.selectedThemeIds.length))
//
//      Logger.debug("  Selected id from themes in history: " + id)
//
//      if (user.profile.questProposalContext.todayReviewedThemeIds.contains(id)) {
//        Logger.debug("Recent list returned theme we've used today, requesting from global one.")
//        themeFromGlobal
//      } else {
//        api.getTheme(GetThemeRequest(id)) match {
//          case OkApiResult(GetThemeResult(theme)) => Some(theme)
//          case _ => themeFromGlobal // If theme in history is removed from our themes - get from global list.
//        }
//      }
//    } else {
//      // Use global list.
//      Logger.debug("Using global list")
//      themeFromGlobal
//    }
//  }

  /**
   *
   */
  def getCoolDownForQuestCreation: Date = {
    import com.github.nscala_time.time.Imports._
    import org.joda.time.DateTime

    val daysToSkip = questProposalPeriod(user.profile.publicProfile.level)

    val tz = DateTimeZone.forOffsetHours(user.profile.publicProfile.bio.timezone)
    (DateTime.now(tz) + daysToSkip.days).hour(constants.FlipHour).minute(0).second(0) toDate ()
  }

  /**
   * Is user can give up quest proposal.
   */
//  def canGiveUpQuestProposal = {
//    if (user.profile.questProposalContext.takenTheme == None)
//      InvalidState
//    else
//      OK
//  }

  /**
   * Reward for approving quest.
   */
  def rewardForMakingApprovedQuest = {
    Assets(rating = ratingForProposalAtLevel(user.profile.publicProfile.level)) * api.config(api.ConfigParams.DebugExpMultiplier).toDouble
  }

  def penaltyForCheatingQuest = {
    (rewardForMakingApprovedQuest * QuestProposalCheatingPenalty) clampTop user.profile.assets
  }

  def penaltyForIACQuest = {
    (rewardForMakingApprovedQuest * QuestProposalIACPenalty) clampTop user.profile.assets
  }

  /**
   *
   */
  def costOfGivingUpQuestProposal = {
    Assets(rating = ratingToGiveUpQuestProposal(user.profile.publicProfile.level)) clampTop user.profile.assets
  }

  /**
   * How much it'll be for a single friend to help us with proposal.
   */
  def costOfAskingForHelpWithProposal = {
    Assets(coins = coinsToInviteFriendForVoteQuestProposal(user.profile.publicProfile.level))
  }

  /**
   * Check is quest deadline passed and quest should be autogave up.
   */
//  def proposalDeadlineReached = {
//    ((user.profile.questProposalContext.takenTheme != None)
//      && user.profile.questProposalContext.questProposalCooldown.before(new Date()))
//  }

}
