package models.domain.quest

import models.domain.common.Assets

case class QuestInfo(
  authorId: String,
  content: QuestInfoContent,
  level: Int,
  vip: Boolean,
  solveCost: Assets,
  solveReward: Assets)

