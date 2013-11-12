package logic

import models.domain._
import controllers.domain.user.protocol.ProfileModificationResult._
import models.domain.Theme
import components.componentregistry.ComponentRegistrySingleton
import scala.util.Random

class UserLogic(val user: User) {

  lazy val db = ComponentRegistrySingleton.db

  /**
   * Check is the user can purchase quest proposals.
   */
  def canPurchaseQuestProposals = {
    if (user.profile.rights.submitPhotoQuests > user.profile.level)
      LevelTooLow
    else if (!(user.profile.assets canAfford costOfPurchasingQuestProposal))
      NotEnoughAssets
    else
      OK
  }

  // TODO implement me.
  def costOfPurchasingQuestProposal = Assets(coins = 10)

  /**
   * Select theme for the user.
   */
  def getRandomThemeForQuestProposal = {
    val themes = db.theme.allThemes

    val rand = new Random(System.currentTimeMillis())
    val random_index = rand.nextInt(themes.length)
    themes(random_index)
  }
}

