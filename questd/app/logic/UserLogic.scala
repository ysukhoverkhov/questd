package logic

import models.domain._
import controllers.domain.DomainAPIComponent
import components.random.RandomComponent
import logic.user._
import logic.user.util._

// This should not go to DB directly since API may have cache layer.
class UserLogic(
    val user: User,
    val api: DomainAPIComponent#DomainAPI,
    val rand: RandomComponent#Random)

    extends CalculatingRights
    with CreatingQuests
    with SolvingQuests
    with VotingQuestProposals
    with VotingQuestSolutions
    with DailyResults
    with Friends
    with Tasks
    with MiscUserLogic
    with QuestSelectUserLogic
    with SolutionSelectUserLogic

    with SelectionHelpers {
}

