package models.store.mongo.dao

import java.util.Date
import models.store.mongo.helpers._
import models.store.dao._
import models.store._
import models.domain._
import models.domain.base._
import play.Logger
import com.mongodb.casbah.commons._

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
   * Update user's session id
   */
  def updateSessionID(id: String, sessionid: String): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        ("$set" -> MongoDBObject(
          "auth.session" -> sessionid))))

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
  def purchaseQuest(id: String, purchasedQuest: QuestInfoWithID, author: PublicProfileWithID, defeatReward: Assets, victoryReward: Assets): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        ("$set" -> MongoDBObject(
          "profile.questSolutionContext.purchasedQuest" -> grater[QuestInfoWithID].asDBObject(purchasedQuest),
          "profile.questSolutionContext.questAuthor" -> grater[PublicProfileWithID].asDBObject(author),
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

  /**
   *
   */
  def purchaseQuestTheme(id: String, purchasedTheme: ThemeWithID, sampleQuest: Option[QuestInfo], approveReward: Assets): Option[User] = {

    val setObject = if (sampleQuest != None) {
      MongoDBObject(
        "profile.questProposalContext.purchasedTheme" -> grater[ThemeWithID].asDBObject(purchasedTheme),
        "profile.questProposalContext.sampleQuest" -> grater[QuestInfo].asDBObject(sampleQuest.get),
        "profile.questProposalContext.approveReward" -> grater[Assets].asDBObject(approveReward))
    } else {
      MongoDBObject(
        "profile.questProposalContext.purchasedTheme" -> grater[ThemeWithID].asDBObject(purchasedTheme),
        "profile.questProposalContext.approveReward" -> grater[Assets].asDBObject(approveReward))
    }

    if (sampleQuest == None) {
      findAndModify(
        id,
        MongoDBObject(
          ("$unset" -> MongoDBObject(
            "profile.questProposalContext.sampleQuest" -> ""))))
    }

    findAndModify(
      id,
      MongoDBObject(
        ("$set" -> setObject),
        ("$inc" -> MongoDBObject(
          "profile.questProposalContext.numberOfPurchasedThemes" -> 1))))
  }

  /**
   *
   */
  def takeQuestTheme(id: String, takenTheme: ThemeWithID, cooldown: Date): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        ("$set" -> MongoDBObject(
          "profile.questProposalContext.numberOfPurchasedThemes" -> 0,
          "profile.questProposalContext.takenTheme" -> grater[ThemeWithID].asDBObject(takenTheme),
          "profile.questProposalContext.questProposalCooldown" -> cooldown)),
        ("$unset" -> MongoDBObject(
          "profile.questProposalContext.purchasedTheme" -> ""))))
  }

  /**
   *
   */
  def resetQuestProposal(id: String): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        ("$set" -> MongoDBObject(
          "profile.questProposalContext.numberOfPurchasedThemes" -> 0)),
        ("$unset" -> MongoDBObject(
          "profile.questProposalContext.purchasedTheme" -> "",
          "profile.questProposalContext.takenTheme" -> "",
          "profile.questProposalContext.sampleQuest" -> ""))))
  }

  /**
   *
   */
  def resetCounters(id: String, resetPurchasesTimeout: Date): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        ("$set" -> MongoDBObject(
          "profile.questSolutionContext.numberOfPurchasedQuests" -> 0,
          "profile.questProposalContext.numberOfPurchasedThemes" -> 0,
          "profile.questProposalVoteContext.numberOfReviewedQuests" -> 0,
          "profile.questSolutionVoteContext.numberOfReviewedSolutions" -> 0,
          "schedules.purchases" -> resetPurchasesTimeout)),
        ("$unset" -> MongoDBObject(
          "profile.questSolutionContext.purchasedQuest" -> "",
          "profile.questProposalContext.purchasedTheme" -> "",
          "profile.questProposalVoteContext.reviewingQuest" -> "",
          "profile.questSolutionVoteContext.reviewingQuestSolution" -> ""))))
  }

  /**
   *
   */
  def addPrivateDailyResult(id: String, dailyResult: DailyResult): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        ("$push" -> MongoDBObject(
          "privateDailyResults" ->
            MongoDBObject(
              "$each" -> List(grater[DailyResult].asDBObject(dailyResult)),
              "$position" -> 0)))))
  }

  /**
   *
   */
  def movePrivateDailyResultsToPublic(id: String, dailyResults: List[DailyResult]): Option[User] = {
    for (a <- 1 to dailyResults.length) {
      findAndModify(
        id,
        MongoDBObject(
          ("$pop" -> MongoDBObject(
            "privateDailyResults" -> 1))))
    }

    findAndModify(
      id,
      MongoDBObject(
        ("$set" -> MongoDBObject(
          "profile.dailyResults" -> dailyResults.map(grater[DailyResult].asDBObject(_))))))
  }

  /**
   *
   */
  def storeProposalInDailyResult(id: String, proposal: QuestProposalResult): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        ("$push" -> MongoDBObject(
          "privateDailyResults.0.decidedQuestProposals" -> grater[QuestProposalResult].asDBObject(proposal)))))
  }

  /**
   *
   */
  def storeSolutionInDailyResult(id: String, solution: QuestSolutionResult): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        ("$push" -> MongoDBObject(
          "privateDailyResults.0.decidedQuestSolutions" -> grater[QuestSolutionResult].asDBObject(solution)))))
  }

  /**
   *
   */
  def levelup(id: String, ratingToNextlevel: Int): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        ("$inc" -> MongoDBObject(
          "profile.publicProfile.level" -> 1,
          "profile.assets.rating" -> -ratingToNextlevel))))
  }

  /**
   *
   */
  def setNextLevelRatingAndRights(id: String, newRatingToNextlevel: Int, rights: Rights): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        ("$set" -> MongoDBObject(
          "profile.ratingToNextLevel" -> newRatingToNextlevel,
          "profile.rights" -> grater[Rights].asDBObject(rights)))))
  }

  /**
   *
   */
  def addFreshDayToHistory(id: String): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        ("$push" -> MongoDBObject(
          "history.votedQuestProposalIds" ->
            MongoDBObject(
              "$each" -> List(List()),
              "$position" -> 0),
          "history.solvedQuestIds" ->
            MongoDBObject(
              "$each" -> List(List()),
              "$position" -> 0),
          "history.votedQuestSolutionIds" ->
            MongoDBObject(
              "$each" -> List(List()),
              "$position" -> 0)))))
  }

  /**
   *
   */
  def removeLastDayFromHistory(id: String): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        ("$pop" -> MongoDBObject(
          "history.votedQuestProposalIds" -> 1,
          "history.solvedQuestIds" -> 1,
          "history.votedQuestSolutionIds" -> 1))))
  }

  /**
   *
   */
  def rememberProposalVotingInHistory(id: String, proposalId: String): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        ("$push" -> MongoDBObject(
          "history.votedQuestProposalIds.0" -> proposalId))))
  }

  /**
   *
   */
  def rememberQuestSolvingInHistory(id: String, questId: String): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        ("$push" -> MongoDBObject(
          "history.solvedQuestIds.0" -> questId))))
  }

  /**
   *
   */
  def rememberSolutionVotingInHistory(id: String, solutionId: String): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        ("$push" -> MongoDBObject(
          "history.votedQuestSolutionIds.0" -> solutionId))))
  }

  /**
   *
   */
  def addToShortlist(id: String, idToAdd: String): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        ("$addToSet" -> MongoDBObject(
          "shortlist" -> idToAdd))))
  }

  /**
   *
   */
  def removeFromShortlist(id: String, idToRemove: String): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        ("$pull" -> MongoDBObject(
          "shortlist" -> idToRemove))))
  }

  /**
   *
   */
  def askFriendship(id: String, idToAdd: String, myFriendship: Friendship, hisFriendship: Friendship): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        ("$push" -> MongoDBObject(
          "friends" -> grater[Friendship].asDBObject(myFriendship)))))

    findAndModify(
      idToAdd,
      MongoDBObject(
        ("$push" -> MongoDBObject(
          "friends" -> grater[Friendship].asDBObject(hisFriendship)))))
  }

}

/**
 * Test version of dao what fails all the time
 */

import com.novus.salat.dao.SalatDAO
import org.bson.types.ObjectId
import models.store.mongo.SalatContext._
import com.mongodb.casbah.MongoConnection

class MongoUserDAOForTest extends MongoUserDAO {
  override val dao = new QSalatDAO[User, ObjectId](collection = MongoConnection("localhost", 55555)("test_db")("test_coll")) {}

}


