package models.domain

import java.util.Date

case class QuestInfoWithID (
    id: String,
    obj: QuestInfo)

case class QuestSolutionContext(
  purchasedQuest: Option[QuestInfoWithID] = None,
  takenQuest: Option[QuestInfoWithID] = None,
  numberOfPurchasedQuests: Int = 0,
  questCooldown: Date = new Date(0))
    

