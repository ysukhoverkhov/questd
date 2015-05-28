package logic

import components.random.RandomComponent
import controllers.domain.DomainAPIComponent
import logic.user._
import logic.user.util._
import models.domain.user.User

// This should not go to DB directly since API may have cache layer.
class UserLogic(
  val user: User,
  val api: DomainAPIComponent#DomainAPI,
  val rand: RandomComponent#Random)

  extends CalculatingRights
  with CreatingQuests
  with SolvingQuests
  with VotingQuests
  with VotingSolutions
  with VotingBattles
  with DailyResults
  with Friends
  with Tasks
  with TimeLineLogic
  with MiscUserLogic
  with CommonUserLogic
  with QuestSelectUserLogic
  with SolutionSelectUserLogic
  with BattleSelectUserLogic

  with SelectionHelpers {
}

