package models.store.mongo.dao.user
import com.mongodb.casbah.commons.MongoDBObject
import com.novus.salat._
import models.domain.common.Assets
import models.domain.user.User
import models.domain.user.dailyresults._
import models.store.dao.user.UserDailyResultsDAO
import models.store.mongo.helpers.BaseMongoDAO
import models.store.mongo.SalatContext._

/**
 * Mongo implementation.
 */
trait MongoUserDailyResultsDAO extends UserDailyResultsDAO {
  this: BaseMongoDAO[User] =>

  /**
   *
   */
  def addPrivateDailyResult(id: String, dailyResult: DailyResult): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$push" -> MongoDBObject(
          "privateDailyResults" ->
            MongoDBObject(
              "$each" -> List(grater[DailyResult].asDBObject(dailyResult)),
              "$position" -> 0))))
  }

  /**
   * @inheritdoc
   */
  def movePrivateDailyResultsToPublic(id: String, dailyResults: List[DailyResult]): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$set" -> MongoDBObject(
          "profile.dailyResults" -> dailyResults.map(grater[DailyResult].asDBObject)),
        "$pull" -> MongoDBObject(
          "privateDailyResults" -> MongoDBObject("$in" -> dailyResults.map(grater[DailyResult].asDBObject)))
      )
    )
  }

  /**
   * @inheritdoc
   */
  def addQuestIncomeToDailyResult(id: String, questIncome: QuestIncome): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$push" -> MongoDBObject(
          "privateDailyResults.0.questsIncome" -> grater[QuestIncome].asDBObject(questIncome))))
  }

  /**
   * @inheritdoc
   */
  def removeQuestIncomeFromDailyResult(id: String, questId: String): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$pull" -> MongoDBObject(
          "privateDailyResults.0.questsIncome" -> MongoDBObject(
            "questId" -> questId))))
  }

  /**
   * @inheritdoc
   */
  def storeQuestSolvingInDailyResult(id: String, questId: String, reward: Assets): Option[User] = {
    findAndModify(
      MongoDBObject(
        "id" -> id,
        "privateDailyResults.0.questsIncome" -> MongoDBObject(
          "$elemMatch" -> MongoDBObject(
            "questId" -> questId
          )
        )),
      MongoDBObject(
        "$inc" -> MongoDBObject(
          "privateDailyResults.0.questsIncome.$.timesSolved" -> 1,
          "privateDailyResults.0.questsIncome.$.solutionsIncome.coins" -> reward.coins,
          "privateDailyResults.0.questsIncome.$.solutionsIncome.money" -> reward.money,
          "privateDailyResults.0.questsIncome.$.solutionsIncome.rating" -> reward.rating)))
  }

  /**
   *
   */
  def storeQuestInDailyResult(id: String, proposal: QuestResult): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$push" -> MongoDBObject(
          "privateDailyResults.0.decidedQuests" -> grater[QuestResult].asDBObject(proposal))))
  }

  /**
   * @inheritdoc
   */
  def storeSolutionInDailyResult(id: String, solution: SolutionResult): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$push" -> MongoDBObject(
          "privateDailyResults.0.decidedSolutions" -> grater[SolutionResult].asDBObject(solution))))
  }

  /**
   * @inheritdoc
   */
  def storeBattleInDailyResult(id: String, battle: BattleResult): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$push" -> MongoDBObject(
          "privateDailyResults.0.decidedBattles" -> grater[BattleResult].asDBObject(battle))))
  }
}
