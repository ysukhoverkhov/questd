package models.store.dao

import models.domain._
import models.domain.view._
import java.util.Date

trait UserDAO extends BaseDAO[User] {

  def updateSessionId(id: String, sessionid: String): Option[User]

  def readBySessionId(sessionid: String): Option[User]
  def readBySNid(snName:String, snid: String): Option[User]

  def addToAssets(id: String, assets: Assets): Option[User]

  def selectQuestSolutionVote(id: String, qsi: QuestSolutionInfoWithID, qsa: PublicProfileWithID, qi: QuestInfoWithID): Option[User]
  def recordQuestSolutionVote(id: String, solutionId: String): Option[User]
  def populateMustVoteSolutionsList(userIds: List[String], solutionId: String): Unit
  def removeMustVoteSolution(id: String, solutionId: String): Option[User]

  def selectQuestProposalVote(id: String, questInfo: QuestInfoWithID, themeInfo: ThemeInfoWithID): Option[User]
  def recordQuestProposalVote(id: String, questId: String, liked: Boolean): Option[User]

  def purchaseQuest(id: String, purchasedQuest: QuestInfoWithID, author: PublicProfileWithID, defeatReward: Assets, victoryReward: Assets): Option[User]
  def takeQuest(id: String, takenQuest: QuestInfoWithID, cooldown: Date, deadline: Date): Option[User]
  def resetQuestSolution(id: String, shouldResetCooldown: Boolean): Option[User]

  def purchaseQuestTheme(id: String, purchasedTheme: ThemeInfoWithID, sampleQuest: Option[QuestInfo], approveReward: Assets): Option[User]
  def takeQuestTheme(id: String, takenTheme: ThemeInfoWithID, cooldown: Date): Option[User]
  def resetQuestProposal(id: String, shouldResetCooldown: Boolean): Option[User]

  def resetPurchases(id: String, resetPurchasesTimeout: Date): Option[User]
  def resetTodayReviewedThemes(id: String): Option[User]

  def addPrivateDailyResult(id: String, dailyResult: DailyResult): Option[User]
  def movePrivateDailyResultsToPublic(id: String, dailyResults: List[DailyResult]): Option[User]
  def storeProposalInDailyResult(id: String, proposal: QuestProposalResult): Option[User]
  def storeSolutionInDailyResult(id: String, solution: QuestSolutionResult): Option[User]
  def storeProposalOutOfTimePenalty(id: String, penalty: Assets): Option[User]
  def storeSolutionOutOfTimePenalty(id: String, penalty: Assets): Option[User]

  def levelup(id: String, ratingToNextlevel: Int): Option[User]
  def setNextLevelRatingAndRights(id: String, newRatingToNextlevel: Int, rights: Rights): Option[User]

  def addFreshDayToHistory(id: String): Option[User]
  def removeLastDayFromHistory(id: String): Option[User]
  def removeLastThemesFromHistory(id: String, themesToRemove: Int): Option[User]
  def removeLastQuestThemesFromHistory(id: String, themesToRemove: Int): Option[User]

  def updateStats(id: String, stats: UserStats): Option[User]

  def addToShortlist(id: String, idToAdd: String): Option[User]
  def removeFromShortlist(id: String, idToRemove: String): Option[User]

  def askFriendship(id: String, idToAdd: String, myFriendship: Friendship, hisFriendship: Friendship): Option[User]
  def updateFriendship(id: String, friendId: String, status: String): Option[User]
  def addFriendship(id: String, friendship: Friendship): Option[User]
  def updateFriendship(id: String, friendId: String, myStatus: String, friendStatus: String): Option[User]
  def removeFriendship(id: String, friendId: String): Option[User]

  def addMessage(id: String, message: Message): Option[User]
  def removeOldestMessage(id: String): Option[User]
  def removeMessage(id: String, messageId: String): Option[User]

  def resetTasks(id: String, newTasks: DailyTasks, resetTasksTimeout: Date): Option[User]
  def addTasks(id: String, newTasks: List[Task], additionalRewaed: Assets): Option[User]
  def incTask(id: String, taskType: String, completed: Float, rewardReceived: Boolean): Option[User]
  def incTutorialTask(id: String, taskId: String, completed: Float, rewardReceived: Boolean): Option[User]

  def updateCultureId(id: String, cultureId: String): Option[User]
  def setGender(id: String, gender: String): Option[User]
  def setDebug(id: String, debug: String): Option[User]
  def setCity(id: String, city: String): Option[User]
  def setCountry(id: String, country: String): Option[User]

  def setTutorialState(id: String, platform: String, state: String): Option[User]
  def addTutorialTaskAssigned(id: String, taskId: String): Option[User]

  def replaceCultureIds(oldCultureId: String, newCultureId: String): Unit
}
