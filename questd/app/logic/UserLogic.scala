package logic

import java.util.Date
import scala.util.Random
import models.domain._
import controllers.domain.user.protocol.ProfileModificationResult._
import components.componentregistry.ComponentRegistrySingleton
import functions._

// This should not go to DB directly since API may have cache layer.
class UserLogic(val user: User) {

  lazy val api = ComponentRegistrySingleton.api

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
    else
      OK
  }

  /**
   * Is user can propose quest.
   */
  def canProposeQuest = {
    if (user.profile.rights.submitPhotoQuests > user.profile.level)
      LevelTooLow
    else if (user.profile.questProposalContext.purchasedTheme == None)
      InvalidState
    else if (user.profile.questProposalContext.questProposalCooldown.after(new Date()))
      CoolDown
    else
      OK
  }
  
  
  /**
   * Tells cost of next theme purchase
   */
  def costOfPurchasingQuestProposal = {
    if (user.profile.questProposalContext.numberOfPurchasedThemes < 4) {
      val c = costToSkipProposal(user.profile.level, user.profile.questProposalContext.numberOfPurchasedThemes + 1)
      Assets(coins = c)
    } else {
      Assets(money = 1)
    }
  }

  /**
   * Select theme for the user to take.
   */
  def getRandomThemeForQuestProposal = {
    val themes = api.allThemes.body.get.themes

    val rand = new Random(System.currentTimeMillis())
    val random_index = rand.nextInt(themes.length)
    themes(random_index)
  }
  
  def getCooldownForTakeTheme: Date = {
    import com.github.nscala_time.time.Imports._
    import org.joda.time.DateTime
    
    val daysToSkipt = questProposalPeriod(user.profile.level)
    
    val tz = DateTimeZone.forOffsetHours(user.profile.bio.timezone)
    (DateTime.now(tz) + daysToSkipt.days).hour(constants.flipHour).minute(0).second(0) toDate()
  }
}

