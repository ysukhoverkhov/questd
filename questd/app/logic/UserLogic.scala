package logic

import models.domain._

class UserLogic (val user: User) {
  
  /**
   * Check is the user can purchase quest proposals.
   */
  // TODO REFACTOR replace with enum here 
  // TODO Implement me.
  def canPurchaseQuestProposals = {
    true
  }
  
  def costOfPurchasingQuestProposal = Cost(10, 0, 0)
}

