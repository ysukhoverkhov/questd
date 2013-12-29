package models.store.dao

import models.domain._
import models.domain.base._

trait UserDAO extends BaseDAO[User] {

  def readBySessionID(sessionid: String): Option[User]
  def readByFBid(fbid: String): Option[User]

  def addToAssets(id: String, assets: Assets): Option[User]

  def selectQuestSolutionVote(id: String, qsi: QuestSolutionInfoWithID, qi: QuestInfo): Option[User]
  def recordQuestSolutionVote(id: String): Option[User]

  def selectQuestProposalVote(id: String, qi: QuestInfoWithID, theme: Theme): Option[User]
  def recordQuestProposalVote(id: String): Option[User]
}

