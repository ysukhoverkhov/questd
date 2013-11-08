package logic

import models.domain._
import controllers.domain.user.ProfileModificationResult._
import models.domain.Theme

// TODO think how to pass db here.
class UserLogic (val user: User) {
  
  /**
   * Check is the user can purchase quest proposals.
   */
  // TODO Implement me.
  def canPurchaseQuestProposals = {
    OK
  }
  
  // TODO implement me.
  def costOfPurchasingQuestProposal = Cost(10, 0, 0)
  
  def getRandomThemeForQuestProposal = Theme("", "This is", "Test theme")
}

