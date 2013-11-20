package models.domain

import java.util.Date

case class QuestContext(
  purchasedQuest: Option[QuestInfo] = None,
  takenQuest: Option[QuestInfo] = None,
  numberOfPurchasedQuests: Int = 0
  //questProposalCooldown: Date = new Date(0))
    
)
