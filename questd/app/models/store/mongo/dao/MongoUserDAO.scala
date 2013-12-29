package models.store.mongo.dao

import java.util.Date
import models.store.mongo.helpers._
import models.store.dao._
import models.store._
import models.domain._
import models.domain.base._
import play.Logger
import com.mongodb.casbah.commons.MongoDBObject

import com.novus.salat._
import models.store.mongo.SalatContext._

/**
 * DOA for User objects
 */
private[mongo] class MongoUserDAO
  extends BaseMongoDAO[User](collectionName = "users")
  with UserDAO {

  /**
   * Read by session id
   */
  def readBySessionID(sessid: String): Option[User] = {
    readByExample("auth.session", sessid)
  }

  /**
   * Read by fb id
   */
  def readByFBid(fbid: String): Option[User] = {
    readByExample("auth.fbid", fbid)
  }

  /**
   * Add assets to profile
   */
  def addToAssets(id: String, assets: Assets): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        ("$inc" -> MongoDBObject(
          "profile.assets.coins" -> assets.coins,
          "profile.assets.money" -> assets.money,
          "profile.assets.rating" -> assets.rating))))
  }

  /**
   *
   */
  def selectQuestSolutionVote(id: String, qsi: QuestSolutionInfoWithID, qi: QuestInfo): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        ("$set" -> MongoDBObject(
          "profile.questSolutionVoteContext.reviewingQuestSolution" -> grater[QuestSolutionInfoWithID].asDBObject(qsi),
          "profile.questSolutionVoteContext.questOfSolution" -> grater[QuestInfo].asDBObject(qi)))))
  }

  /**
   *
   */
  def recordQuestSolutionVote(id: String): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        ("$inc" -> MongoDBObject(
          "profile.questSolutionVoteContext.numberOfReviewedSolutions" -> 1)),
        ("$unset" -> MongoDBObject(
          "profile.questSolutionVoteContext.reviewingQuestSolution" -> "",
          "profile.questSolutionVoteContext.questOfSolution" -> ""))))
  }

  /**
   *
   */
  def selectQuestProposalVote(id: String, qi: QuestInfoWithID, theme: Theme): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        ("$set" -> MongoDBObject(
          "profile.questProposalVoteContext.reviewingQuest" -> grater[QuestInfoWithID].asDBObject(qi),
          "profile.questProposalVoteContext.themeOfQuest" -> grater[Theme].asDBObject(theme)))))
  }

  /**
   *
   */
  def recordQuestProposalVote(id: String): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        ("$inc" -> MongoDBObject(
          "profile.questProposalVoteContext.numberOfReviewedQuests" -> 1)),
        ("$unset" -> MongoDBObject(
          "profile.questProposalVoteContext.reviewingQuest" -> "",
          "profile.questProposalVoteContext.themeOfQuest" -> ""))))
  }

  /**
   *
   */
  def purchaseQuest(id: String, purchasedQuest: QuestInfoWithID, author: BioWithID, defeatReward: Assets, victoryReward: Assets): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        ("$set" -> MongoDBObject(
          "profile.questSolutionContext.purchasedQuest" -> grater[QuestInfoWithID].asDBObject(purchasedQuest),
          "profile.questSolutionContext.questAuthor" -> grater[BioWithID].asDBObject(author),
          "profile.questSolutionContext.defeatReward" -> grater[Assets].asDBObject(defeatReward),
          "profile.questSolutionContext.victoryReward" -> grater[Assets].asDBObject(victoryReward))),
        ("$inc" -> MongoDBObject(
          "profile.questSolutionContext.numberOfPurchasedQuests" -> 1,
          "stats.questsReviewed" -> 1))))
  }

  /**
   *
   */
  def takeQuest(id: String, takenQuest: QuestInfoWithID, cooldown: Date, deadline: Date): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        ("$set" -> MongoDBObject(
          "profile.questSolutionContext.numberOfPurchasedQuests" -> 0,
          "profile.questSolutionContext.takenQuest" -> grater[QuestInfoWithID].asDBObject(takenQuest),
          "profile.questSolutionContext.questCooldown" -> cooldown,
          "profile.questSolutionContext.questDeadline" -> deadline)),
        ("$unset" -> MongoDBObject(
          "profile.questSolutionContext.purchasedQuest" -> "")),
        ("$inc" -> MongoDBObject(
          "stats.questsAccepted" -> 1))))
  }

  /**
   *
   */
  def resetQuestSolution(id: String): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        ("$set" -> MongoDBObject(
          "profile.questSolutionContext.numberOfPurchasedQuests" -> 0)),
        ("$unset" -> MongoDBObject(
          "profile.questSolutionContext.purchasedQuest" -> "",
          "profile.questSolutionContext.takenQuest" -> "",
          "profile.questSolutionContext.questAuthor" -> ""))))
  }
}

/**
 * Test version of dao what fails al the time
 */

import com.novus.salat.dao.SalatDAO
import org.bson.types.ObjectId
import models.store.mongo.SalatContext._
import com.mongodb.casbah.MongoConnection

class MongoUserDAOForTest extends MongoUserDAO {
  override val dao = new QSalatDAO[User, ObjectId](collection = MongoConnection("localhost", 55555)("test_db")("test_coll")) {}

}


