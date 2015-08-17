package models.view

import models.domain.battle.BattleInfo

case class BattleView (
  id: String,
  info: BattleInfo,
  myVotedSolutionId: Option[String]
  )

