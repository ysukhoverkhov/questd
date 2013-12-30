package models.store.dao

import models.domain._
import models.domain.base._
import java.util.Date

trait UserDAO extends BaseDAO[User] {

  def readBySessionID(sessionid: String): Option[User]
  def readByFBid(fbid: String): Option[User]

  def addToAssets(id: String, assets: Assets): Option[User]

  def selectQuestSolutionVote(id: String, qsi: QuestSolutionInfoWithID, qi: QuestInfo): Option[User]
  def recordQuestSolutionVote(id: String): Option[User]

  def selectQuestProposalVote(id: String, qi: QuestInfoWithID, theme: Theme): Option[User]
  def recordQuestProposalVote(id: String): Option[User]
  
  def purchaseQuest(id: String, purchasedQuest: QuestInfoWithID, author: BioWithID, defeatReward: Assets, victoryReward: Assets): Option[User]
  def takeQuest(id: String, takenQuest: QuestInfoWithID, cooldown: Date, deadline: Date): Option[User]
  def resetQuestSolution(id: String): Option[User]
  
  def purchaseQuestTheme(id: String, purchasedTheme: ThemeWithID, sampleQuest: Option[QuestInfo], approveReward: Assets): Option[User]
  def takeQuestTheme(id: String, takenTheme: ThemeWithID, cooldown: Date): Option[User]
  def resetQuestProposal(id: String): Option[User]
  
  def resetCounters(id: String, resetPurchasesTimeout: Date): Option[User]
}

