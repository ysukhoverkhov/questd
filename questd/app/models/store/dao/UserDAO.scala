package models.store.dao

import models.domain._
import models.domain.base._
import java.util.Date

trait UserDAO extends BaseDAO[User] {

  def updateSessionID(id: String, sessionid: String): Option[User]
  
  def readBySessionID(sessionid: String): Option[User]
  def readByFBid(fbid: String): Option[User]

  def addToAssets(id: String, assets: Assets): Option[User]

  def selectQuestSolutionVote(id: String, qsi: QuestSolutionInfoWithID, qi: QuestInfo): Option[User]
  def recordQuestSolutionVote(id: String): Option[User]

  def selectQuestProposalVote(id: String, qi: QuestInfoWithID, theme: Theme): Option[User]
  def recordQuestProposalVote(id: String): Option[User]
  
  def purchaseQuest(id: String, purchasedQuest: QuestInfoWithID, author: PublicProfileWithID, defeatReward: Assets, victoryReward: Assets): Option[User]
  def takeQuest(id: String, takenQuest: QuestInfoWithID, cooldown: Date, deadline: Date): Option[User]
  def resetQuestSolution(id: String): Option[User]
  
  def purchaseQuestTheme(id: String, purchasedTheme: ThemeWithID, sampleQuest: Option[QuestInfo], approveReward: Assets): Option[User]
  def takeQuestTheme(id: String, takenTheme: ThemeWithID, cooldown: Date): Option[User]
  def resetQuestProposal(id: String): Option[User]
  
  def resetCounters(id: String, resetPurchasesTimeout: Date): Option[User]
  
  def addPrivateDailyResult(id: String, dailyResult: DailyResult): Option[User]
  def movePrivateDailyResultsToPublic(id: String, dailyResults: List[DailyResult]): Option[User]
  def storeProposalInDailyResult(id: String, proposal: QuestProposalResult): Option[User]
  def storeSolutionInDailyResult(id: String, solution: QuestSolutionResult): Option[User]

  def levelup(id: String, ratingToNextlevel: Int): Option[User]
  def setNextLevelRatingAndRights(id: String, newRatingToNextlevel: Int, rights: Rights): Option[User]
  
  def addFreshDayToHistory(id: String): Option[User]
  def removeLastDayFromHistory(id: String): Option[User]
  def rememberProposalVotingInHistory(id: String, proposalId: String): Option[User]
  def rememberQuestSolvingInHistory(id: String, questId: String): Option[User]
  def rememberSolutionVotingInHistory(id: String, solutionId: String): Option[User]
  
  def addToShortlist(id: String, idToAdd: String): Option[User]
  def removeFromShortlist(id: String, idToRemove: String): Option[User]
  
  def askFriendship(id: String, idToAdd: String, myFriendship: Friendship, hisFriendship: Friendship): Option[User]
  def updateFriendship(id: String, friendId: String, status: String): Option[User]
}

