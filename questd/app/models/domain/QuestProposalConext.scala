package models.domain

import java.util.Date

case class QuestProposalConext(
  purchasedTheme: Option[Theme] = None,
  takenTheme: Option[Theme] = None,
  numberOfPurchasedThemes: Int = 0,
  questProposalCooldown: Date = new Date(0))
    
