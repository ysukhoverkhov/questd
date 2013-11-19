package models.domain

import java.util.Date

case class QuestContext(
  purchasedQuest: Option[Quest] = None,
  //takenTheme: Option[Theme] = None,
  numberOfPurchasedQuests: Int = 0
  //questProposalCooldown: Date = new Date(0))
    
)