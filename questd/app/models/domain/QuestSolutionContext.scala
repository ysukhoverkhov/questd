package models.domain

import java.util.Date
import models.domain.base._

case class QuestSolutionContext(
  defeatReward: Assets = Assets(),
  victoryReward: Assets = Assets(),
  purchasedQuest: Option[QuestInfoWithID] = None,
  takenQuest: Option[QuestInfoWithID] = None,
  numberOfPurchasedQuests: Int = 0,
  questCooldown: Date = new Date(0),
  questDeadline: Date = new Date(0))
    
