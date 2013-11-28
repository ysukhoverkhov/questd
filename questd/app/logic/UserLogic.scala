package logic

import java.util.Date
import scala.util.Random
import com.github.nscala_time.time.Imports._
import org.joda.time.DateTime
import models.domain._
import models.domain.ContentType._
import controllers.domain.user.protocol.ProfileModificationResult._
import components.componentregistry.ComponentRegistrySingleton
import functions._
import constants._
import play.Logger

// This should not go to DB directly since API may have cache layer.
class UserLogic(val user: User) {

  lazy val api = ComponentRegistrySingleton.api

  /**
   * **************************
   * Proposing quests.
   * **************************
   */

  /**
   * Check is the user can purchase quest proposals.
   */
  def canPurchaseQuestProposals = {
    if (user.profile.rights.submitPhotoQuests > user.profile.level)
      LevelTooLow
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
    if (user.profile.rights.submitPhotoQuests > user.profile.level)
      LevelTooLow
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
    val level = conentType match {
      case Photo => user.profile.rights.submitPhotoQuests
      case Video => user.profile.rights.submitVideoQuests
    }

    if (level > user.profile.level)
      LevelTooLow
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
      val c = costToSkipProposal(user.profile.level, user.profile.questProposalContext.numberOfPurchasedThemes + 1)
      Assets(coins = c)
    } else {
      Assets(money = 1)
    }
  }

  /**
   * Cost of proposing quest.
   */
  def costOfTakingQuestTheme = {
    Assets(coins = costToTakeQuestTheme(user.profile.level))
  }

  /**
   * Select theme for the user to take.
   */
  def getRandomThemeForQuestProposal = {
    val themes = api.allThemes.body.get.themes

    if (themes.size > 0) {
      val rand = new Random(System.currentTimeMillis())
      val random_index = rand.nextInt(themes.length)
      themes(random_index)
    } else {
      Logger.error("No themes in database, stub theme returned")
      Theme()
    }
  }

  /**
   *
   */
  def getCooldownForTakeTheme: Date = {
    import com.github.nscala_time.time.Imports._
    import org.joda.time.DateTime

    val daysToSkipt = questProposalPeriod(user.profile.level)

    val tz = DateTimeZone.forOffsetHours(user.profile.bio.timezone)
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
   *
   */
  def costOfGivingUpQuestProposal = {
    Assets(rating = ratingToGiveUpQuestProposal(user.profile.level)) clampTop user.profile.assets
  }

  /**
   * **********************************
   * Solving quests
   * **********************************
   */

  /**
   * Check can the user purchase quest.
   */
  def canPurchaseQuest = {
    if (user.profile.rights.submitPhotoResults > user.profile.level)
      LevelTooLow
    else if (!(user.profile.assets canAfford costOfPurchasingQuest))
      NotEnoughAssets
    else if (user.profile.questContext.questCooldown.after(new Date()))
      CoolDown
    else if (user.profile.questContext.takenQuest != None)
      InvalidState
    else
      OK
  }

  /**
   * Tells cost of next theme purchase
   */
  def costOfPurchasingQuest = {
    if (user.profile.questContext.numberOfPurchasedQuests < numberOfQuestsSkipsForCoins) {

      val questDuration = user.profile.questContext.purchasedQuest match {
        case Some(QuestInfoWithID(_, q)) => q.duration
        case _ => 1
      }

      val c = costToSkipQuest(user.profile.level, user.profile.questContext.numberOfPurchasedQuests + 1, questDuration)
      Assets(coins = c)
    } else {
      Assets(money = 1)
    }
  }

  /**
   * Takes everything into account and returns possible quest to be solved by user.
   */
  def getRandomQuestForSolution: Quest = {
    val quests = api.allQuestsInRotation.body.get.quests

    if (quests.length == 0) {
      Quest(info = QuestInfo(ContentReference(0, "this", "is a stub quest since no quests are in db")), userID = "userID")
    } else {
      val rand = new Random(System.currentTimeMillis())
      val random_index = rand.nextInt(quests.length)
      quests(random_index)
    }
  }

  /**
   * Check are we able to take quest.
   */
  def canTakeQuest = {
    if (user.profile.rights.submitPhotoResults > user.profile.level)
      LevelTooLow
    else if (user.profile.questContext.purchasedQuest == None)
      InvalidState
    else if (!(user.profile.assets canAfford costOfTakingQuest))
      NotEnoughAssets
    else
      OK
  }

  /**
   * Get cost of taking quest to resolve.
   */
  def costOfTakingQuest = {
    val questDuration: Int =
      if (user.profile.questContext.purchasedQuest == None)
        0
      else
        user.profile.questContext.purchasedQuest.get.obj.duration

    Assets(coins = costToTakeQuestToSolve(user.profile.level, questDuration))
  }

  /**
   * Is user can propose quest of given type.
   */
  def canResulveQuest(conentType: ContentType) = {
    val level = conentType match {
      case Photo => user.profile.rights.submitPhotoResults
      case Video => user.profile.rights.submitVideoResults
    }

    if (level > user.profile.level)
      LevelTooLow
    else if (user.profile.questContext.takenQuest == None)
      InvalidState
    else
      OK
  }

  /**
   * Is user can give up quest.
   */
  def canGiveUpQuest = {
    if (user.profile.questContext.takenQuest == None)
      InvalidState
    else
      OK
  }

  /**
   * How much it'll cost to give up quest.
   */
  def costOfGivingUpQuest = {
    val duration = user.profile.questContext.takenQuest match {
      case Some(QuestInfoWithID(_, i)) => i.duration
      case None => 0
    }

    Assets(rating = ratingToGiveUpQuest(user.profile.level, duration)) clampTop user.profile.assets
  }

  /**
   * Cooldown for taking quest.
   */
  def getCooldownForTakeQuest(qi: QuestInfo) = {
    val daysToSkipt = qi.duration

    val tz = DateTimeZone.forOffsetHours(user.profile.bio.timezone)
    (DateTime.now(tz) + daysToSkipt.days).hour(constants.flipHour).minute(0).second(0) toDate ()
  }

  /**
   * Cooldown for reseting purchases. Purchases should be reset in nearest 5am at user's time.
   */
  def getResetPurchasesTimeout = {
    val tz = DateTimeZone.forOffsetHours(user.profile.bio.timezone)
    (DateTime.now(tz) + 1.day).hour(constants.flipHour).minute(0).second(0) toDate ()
  }
}

