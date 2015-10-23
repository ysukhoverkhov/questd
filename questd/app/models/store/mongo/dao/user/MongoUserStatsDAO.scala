package models.store.mongo.dao.user
import com.mongodb.casbah.commons.MongoDBObject
import com.novus.salat._
import models.domain.common.ContentVote
import models.domain.user.User
import models.domain.user.stats.SolutionsInBattle
import models.store.dao.user.UserStatsDAO
import models.store.mongo.helpers.BaseMongoDAO
import models.store.mongo.SalatContext._

/**
 * Mongo implementation.
 */
trait MongoUserStatsDAO extends UserStatsDAO {
  this: BaseMongoDAO[User] =>

  /**
   * @inheritdoc
   */
  def recordQuestCreation(id: String, questId: String): Option[User] = {
    val queryBuilder = MongoDBObject.newBuilder

    queryBuilder += ("$push" -> MongoDBObject(
      "stats.createdQuests" -> questId))

    findAndModify(
      id,
      queryBuilder.result())
  }

  /**
   * @inheritdoc
   */
  def recordQuestSolving(id: String, questId: String, solutionId: String, removeBookmark: Boolean): Option[User] = {

    val queryBuilder = MongoDBObject.newBuilder

    if (removeBookmark) {
      queryBuilder += ("$unset" -> MongoDBObject(
        "profile.questSolutionContext.bookmarkedQuest" -> ""))
    }

    queryBuilder += ("$set" -> MongoDBObject(
      s"stats.solvedQuests.$questId" -> solutionId))

    findAndModify(
      id,
      queryBuilder.result())
  }

  /**
   * @inheritdoc
   */
  def recordQuestVote(id: String, questId: String, vote: ContentVote.Value): Option[User] = {
    val queryBuilder = MongoDBObject.newBuilder

    queryBuilder += ("$set" -> MongoDBObject(
      s"stats.votedQuests.$questId" -> vote.toString))

    findAndModify(
      MongoDBObject(
        "id" -> id),
      queryBuilder.result())
  }

  /**
   * @inheritdoc
   */
  def recordSolutionVote(id: String, solutionId: String, vote: ContentVote.Value): Option[User] = {
    val queryBuilder = MongoDBObject.newBuilder

    queryBuilder += ("$set" -> MongoDBObject(
      s"stats.votedSolutions.$solutionId" -> vote.toString))

    findAndModify(
      MongoDBObject(
        "id" -> id),
      queryBuilder.result())
  }

  /**
   * @inheritdoc
   */
  def recordBattleVote(id: String, battleId: String, solutionId: String): Option[User] = {
    val queryBuilder = MongoDBObject.newBuilder

    queryBuilder += ("$set" -> MongoDBObject(
      s"stats.votedBattles.$battleId" -> solutionId))

    findAndModify(
      MongoDBObject(
        "id" -> id),
      queryBuilder.result())
  }

  /**
   * @inheritdoc
   */
  def recordBattleParticipation(id: String, battleId: String, rivalSolutionIds: SolutionsInBattle): Option[User] = {
    val queryBuilder = MongoDBObject.newBuilder

    queryBuilder += ("$set" -> MongoDBObject(
      s"stats.participatedBattles.$battleId" -> grater[SolutionsInBattle].asDBObject(rivalSolutionIds)))

    findAndModify(
      MongoDBObject(
        "id" -> id),
      queryBuilder.result())
  }

  /**
   * @inheritdoc
   */
  def setFriendsNotifiedAboutRegistrationFlag(id: String, flag: Boolean): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$set" -> MongoDBObject(
          "stats.friendsNotifiedAboutRegistration" -> flag)))
  }
}
