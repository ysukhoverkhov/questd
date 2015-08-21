package models.domain.quest

import java.util.Date

import models.domain.common.Assets

case class QuestInfo(
  authorId: String,
  content: QuestInfoContent,
  level: Int,
  vip: Boolean,
  solveCost: Assets,
  solveReward: Assets,
  victoryReward: Assets,
  defeatReward: Assets,
  creationDate: Date = new Date()
  )

