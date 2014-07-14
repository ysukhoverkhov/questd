package logic.user

import java.util.Date
import play.Logger
import logic._
import logic.constants._
import logic.functions._
import controllers.domain.app.protocol.ProfileModificationResult._
import models.domain._
import models.domain.ContentType._
import controllers.domain.admin._
import controllers.domain._

/**
 * All logic related to proposing quests.
 */
trait ProposingQuests { this: UserLogic =>

  /**
   * Check is the user can purchase quest proposals.
   */
  def canPurchaseQuestProposals = {
    if (!user.profile.rights.unlockedFunctionality.contains(Functionality.SubmitPhotoQuests.toString()))
      NotEnoughRights
    else if (!(user.profile.assets canAfford costOfPurchasingQuestProposal))
      NotEnoughAssets
    else if (user.profile.questProposalContext.questProposalCooldown.after(new Date()))
      CoolDown
    else if (user.profile.questProposalContext.takenTheme != None)
      InvalidState
    else
      OK
  }

  /**
   * Is user can propose quest of given type.
   */
  def canTakeQuestTheme = {
    if (!user.profile.rights.unlockedFunctionality.contains(Functionality.SubmitPhotoQuests.toString()))
      NotEnoughRights
    else if (user.profile.questProposalContext.purchasedTheme == None)
      InvalidState
    else if (!(user.profile.assets canAfford costOfTakingQuestTheme))
      NotEnoughAssets
    else
      OK
  }

  /**
   * Is user can propose quest of given type.
   */
  def canProposeQuest(conentType: ContentType) = {
    val content = conentType match {
      case Photo => user.profile.rights.unlockedFunctionality.contains(Functionality.SubmitPhotoQuests.toString())
      case Video => user.profile.rights.unlockedFunctionality.contains(Functionality.SubmitVideoQuests.toString())
    }

    if (!content)
      NotEnoughRights
    else if (user.profile.questProposalContext.takenTheme == None)
      InvalidState
    else
      OK
  }

  /**
   * Tells cost of next theme purchase
   */
  def costOfPurchasingQuestProposal = {
    if (user.profile.questProposalContext.numberOfPurchasedThemes < numberOfThemesSkipsForCoins) {
      val c = costToSkipProposal(user.profile.publicProfile.level, user.profile.questProposalContext.numberOfPurchasedThemes + 1)
      Assets(coins = c)
    } else {
      Assets(money = 1)
    }
  }

  /**
   * Cost of proposing quest.
   */
  def costOfTakingQuestTheme = {
    Assets(coins = costToTakeQuestTheme(user.profile.publicProfile.level))
  }

  /**
   * Select theme for the user to take.
   */
  def getRandomThemeForQuestProposal(themesCount: Long): Option[Theme] = {

    def themeFromGlobal = {
      val themes = api.allThemes(AllThemesRequest(sorted = true)).body.get.themes

      selectTheme(themes, user.profile.questProposalContext.todayReviewedThemeIds)
    }

    val probabilityOfRecentList = {
      val maxShare = api.config(api.ConfigParams.FavoriteThemesShare).toDouble
      val minShare = maxShare / 4
      val ourShare = user.history.selectedThemeIds.length / themesCount.toDouble

      if (minShare > ourShare)
        0
      else
        Math.min(1, (ourShare - minShare) / (maxShare - minShare)) * api.config(api.ConfigParams.FavoriteThemesProbability).toDouble
    }

    Logger.debug("Probability of recent list " + probabilityOfRecentList)

    if (rand.nextDouble < probabilityOfRecentList) {
      // Use recent list
      Logger.debug("Using recent list")
      val id = user.history.selectedThemeIds(rand.nextInt(user.history.selectedThemeIds.length))

      Logger.trace("  Selected id from themes in history: " + id)

      if (user.profile.questProposalContext.todayReviewedThemeIds.contains(id)) {
        Logger.debug("Recent list returned theme we've used today, requesting from global one.")
        themeFromGlobal
      } else {
        api.getTheme(GetThemeRequest(id)) match {
          case OkApiResult(Some(GetThemeResult(theme))) => Some(theme)
          case _ => None
        }
      }
    } else {
      // Use global list.
      Logger.debug("Using global list")
      themeFromGlobal
    }
  }

  /**
   *
   */
  def getCooldownForTakeTheme: Date = {
    import com.github.nscala_time.time.Imports._
    import org.joda.time.DateTime

    val daysToSkipt = questProposalPeriod(user.profile.publicProfile.level)

    val tz = DateTimeZone.forOffsetHours(user.profile.publicProfile.bio.timezone)
    (DateTime.now(tz) + daysToSkipt.days).hour(constants.flipHour).minute(0).second(0) toDate ()
  }

  /**
   * Is user can give up quest proposal.
   */
  def canGiveUpQuestProposal = {
    if (user.profile.questProposalContext.takenTheme == None)
      InvalidState
    else
      OK
  }

  /**
   * Reward for approving quest.
   */
  def rewardForMakingApprovedQuest = {
    Assets(rating = ratingForProposalAtLevel(user.profile.publicProfile.level)) * api.config(api.ConfigParams.DebugExpMultiplier).toDouble
  }

  def penaltyForCheatingQuest = {
    (rewardForMakingApprovedQuest * questProposalCheatingPenalty) clampTop user.profile.assets
  }

  def penaltyForIACQuest = {
    (rewardForMakingApprovedQuest * questProposalIACPenalty) clampTop user.profile.assets
  }

  /**
   *
   */
  def costOfGivingUpQuestProposal = {
    Assets(rating = ratingToGiveUpQuestProposal(user.profile.publicProfile.level)) clampTop user.profile.assets
  }

  /**
   * Check is quest deadline passed and quest should be autogave up.
   */
  def proposalDeadlineReached = {
    ((user.profile.questProposalContext.takenTheme != None)
      && (user.profile.questProposalContext.questProposalCooldown.before(new Date())))
  }

  /**
   * Select theme from returned iterator what is not contained in given list.
   */
  private def selectTheme(i: Iterator[Theme], usedThemesIds: List[String]): Option[Theme] = {
    Logger.trace("In selectTheme")
    if (i.hasNext) {
      val t = i.next()

      Logger.trace("Checking used themes to have reviewed theme in: " + t.id + " IN " + usedThemesIds)
      if (!usedThemesIds.contains(t.id)) {
        Logger.trace("  Theme selected: " + t.id)
        Some(t)
      } else {
        selectTheme(i, usedThemesIds)
      }
    } else {
      None
    }
  }

}