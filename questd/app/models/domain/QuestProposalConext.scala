package models.domain

import java.util.Date
import models.domain.base._

case class QuestProposalConext(
  approveReward: Assets = Assets(),
  purchasedTheme: Option[ThemeWithID] = None,
  takenTheme: Option[ThemeWithID] = None,
  sampleQuest: Option[QuestInfo] = None,
  numberOfPurchasedThemes: Int = 0,
  questProposalCooldown: Date = new Date(0))
    
