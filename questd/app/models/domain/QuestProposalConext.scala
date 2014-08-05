package models.domain

import java.util.Date
import models.domain.view._

case class QuestProposalConext(
  approveReward: Assets = Assets(),
  purchasedTheme: Option[ThemeWithID] = None,
  takenTheme: Option[ThemeWithID] = None,
  todayReviewedThemeIds: List[String] = List(),
  sampleQuest: Option[QuestInfo] = None,
  numberOfPurchasedThemes: Int = 0,
  questProposalCooldown: Date = new Date(0))
    
