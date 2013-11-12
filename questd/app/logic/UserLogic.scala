package logic

import models.domain._
import controllers.domain.user.protocol.ProfileModificationResult._
import models.domain.Theme
import components.componentregistry.ComponentRegistrySingleton
import scala.util.Random

import functions._

// This should not go to DB directly since API may have cache layer.
class UserLogic(val user: User) {

  lazy val api = ComponentRegistrySingleton.api

  /**
   * Check is the user can purchase quest proposals.
   * TODO add check for required amount of time to pass since last quest proposal. and do not forget to write test about it.
   */
  def canPurchaseQuestProposals = {
    if (user.profile.rights.submitPhotoQuests > user.profile.level)
      LevelTooLow
    else if (!(user.profile.assets canAfford costOfPurchasingQuestProposal))
      NotEnoughAssets
    else
      OK
  }

  /**
   * Tells cost of next theme purchase
   */
  def costOfPurchasingQuestProposal = {
    if (user.questProposalContext.numberOfPurchasedThemes < 4) {
      val c = costToSkipProposal(user.profile.level, user.questProposalContext.numberOfPurchasedThemes + 1)
      Assets(coins = c)
    } else {
      Assets(money = 1)
    }
  }

  /**
   * Select theme for the user.
   */
  def getRandomThemeForQuestProposal = {
    val themes = api.allThemes.body.get.themes

    val rand = new Random(System.currentTimeMillis())
    val random_index = rand.nextInt(themes.length)
    themes(random_index)
  }
}

