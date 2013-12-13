package models.domain

import java.util.Date

case class QuestProposalConext(
  approveReward: Assets = Assets(),
  purchasedTheme: Option[Theme] = None,
  takenTheme: Option[Theme] = None,
  numberOfPurchasedThemes: Int = 0,
  questProposalCooldown: Date = new Date(0))
    
// TODO: calculate approve reward on purchasing theme and do not recalculate it after that.
