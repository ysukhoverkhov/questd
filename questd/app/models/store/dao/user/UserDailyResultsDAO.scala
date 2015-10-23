package models.store.dao.user

import models.domain.common.Assets
import models.domain.user.User
import models.domain.user.dailyresults._

/**
 * DAO for user's daily results.
 */
trait UserDailyResultsDAO {

  def addPrivateDailyResult(id: String, dailyResult: DailyResult): Option[User]

  def movePrivateDailyResultsToPublic(id: String, dailyResults: List[DailyResult]): Option[User]

  def addQuestIncomeToDailyResult(id: String, questIncome: QuestIncome): Option[User]

  def removeQuestIncomeFromDailyResult(id: String, questId: String): Option[User]

  def storeQuestSolvingInDailyResult(id: String, questId: String, reward: Assets): Option[User]

  def storeQuestInDailyResult(id: String, proposal: QuestResult): Option[User]

  def storeSolutionInDailyResult(id: String, solution: SolutionResult): Option[User]

  def storeBattleInDailyResult(id: String, battle: BattleResult): Option[User]
}
