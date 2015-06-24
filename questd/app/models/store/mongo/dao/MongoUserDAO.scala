package models.store.mongo.dao

import java.util.Date

import com.mongodb.casbah.commons._
import com.novus.salat._
import models.domain.common.{Assets, ContentVote}
import models.domain.user._
import models.domain.user.auth.CrossPromotedApp
import models.domain.user.battlerequests.BattleRequest
import models.domain.user.dailyresults._
import models.domain.user.friends.Friendship
import models.domain.user.message.Message
import models.domain.user.profile.{DailyTasks, Rights, Task}
import models.domain.user.stats.SolutionsInBattle
import models.domain.user.timeline.TimeLineEntry
import models.store.dao._
import models.store.mongo.SalatContext._
import models.store.mongo.helpers._
import models.view.QuestView

/**
 * DOA for User objects
 */
private[mongo] class MongoUserDAO
  extends BaseMongoDAO[User](collectionName = "users")
  with UserDAO {

  /**
   * Read by session id
   */
  def readBySessionId(sessionId: String): Option[User] = {
    readByExample("auth.session", sessionId)
  }

  /**
   * Read by fb id
   */
  def readBySNid(snName: String, userId: String): Option[User] = {
    readByExample(
      MongoDBObject(
        "auth.loginMethods.methodName" -> snName,
        "auth.loginMethods.userId" -> userId
      ))
  }

  /**
   * Add assets to profile
   */
  def addToAssets(id: String, assets: Assets): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$inc" -> MongoDBObject(
          "profile.assets.coins" -> assets.coins,
          "profile.assets.money" -> assets.money,
          "profile.assets.rating" -> assets.rating)))
  }

  /**
   * Update user's session id
   */
  def updateSessionId(id: String, sessionid: String): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$set" -> MongoDBObject(
          "auth.session" -> sessionid,
          "auth.lastLogin" -> new Date)))
  }

  /**
   * @inheritdoc
   */
  def addCrossPromotions(id: String, snName: String, apps: List[CrossPromotedApp]): Option[User] = {
    findAndModify(
      MongoDBObject(
        "id" -> id,
        "auth.loginMethods.methodName" -> snName),
      MongoDBObject(
        "$addToSet" -> MongoDBObject(
          "auth.loginMethods.$.crossPromotion.apps" -> MongoDBObject(
            "$each" -> apps.map(grater[CrossPromotedApp].asDBObject)))))
  }

  /**
   * @inheritdoc
   */
  def populateMustVoteSolutionsList(userIds: List[String], solutionId: String): Unit = {
    update(
      query = MongoDBObject(
        "id" -> MongoDBObject(
          "$in" -> userIds
        )),
      updateRules = MongoDBObject(
        "$addToSet" -> MongoDBObject(
          "mustVoteSolutions" -> solutionId)),
      multi = true)
  }

  /**
   *
   */
  def removeMustVoteSolution(id: String, solutionId: String): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$pull" -> MongoDBObject(
          "mustVoteSolutions" -> solutionId)))
  }


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
  def setQuestBookmark(id: String, quest: QuestView): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$set" -> MongoDBObject(
          "profile.questSolutionContext.bookmarkedQuest" -> grater[QuestView].asDBObject(quest))
      ))
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
  def updateQuestCreationCoolDown(id: String, coolDown: Date): Option[User] = {
    val queryBuilder = MongoDBObject.newBuilder

    queryBuilder += ("$set" -> MongoDBObject(
      "profile.questCreationContext.questCreationCoolDown" -> coolDown))

    findAndModify(
      id,
      queryBuilder.result())
  }

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
   *
   */
  def movePrivateDailyResultsToPublic(id: String, dailyResults: List[DailyResult]): Option[User] = {
    for (a <- 1 to dailyResults.length) {
      findAndModify(
        id,
        MongoDBObject(
          "$pop" -> MongoDBObject(
            "privateDailyResults" -> 1)))
    }

    findAndModify(
      id,
      MongoDBObject(
        "$set" -> MongoDBObject(
          "profile.dailyResults" -> dailyResults.map(grater[DailyResult].asDBObject))))
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

  /**
   * @inheritdoc
   */
  def levelUp(id: String, ratingToNextLevel: Int): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$inc" -> MongoDBObject(
          "profile.publicProfile.level" -> 1,
          "profile.assets.rating" -> -ratingToNextLevel)))
  }

  /**
   *
   */
  def setNextLevelRatingAndRights(id: String, newRatingToNextLevel: Int, rights: Rights): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$set" -> MongoDBObject(
          "profile.ratingToNextLevel" -> newRatingToNextLevel,
          "profile.rights" -> grater[Rights].asDBObject(rights))))
  }

  /**
   *
   */
  def addToFollowing(id: String, idToAdd: String): Option[User] = {
    findAndModify(
      idToAdd,
      MongoDBObject(
        "$addToSet" -> MongoDBObject(
          "followers" -> id)))

    findAndModify(
      id,
      MongoDBObject(
        "$addToSet" -> MongoDBObject(
          "following" -> idToAdd)))
  }

  /**
   *
   */
  def removeFromFollowing(id: String, idToRemove: String): Option[User] = {
    findAndModify(
      idToRemove,
      MongoDBObject(
        "$pull" -> MongoDBObject(
          "followers" -> id)))

    findAndModify(
      id,
      MongoDBObject(
        "$pull" -> MongoDBObject(
          "following" -> idToRemove)))
  }

  /**
   *
   */
  def askFriendship(id: String, idToAdd: String, myFriendship: Friendship, hisFriendship: Friendship): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$push" -> MongoDBObject(
          "friends" -> grater[Friendship].asDBObject(myFriendship))))

    findAndModify(
      idToAdd,
      MongoDBObject(
        "$push" -> MongoDBObject(
          "friends" -> grater[Friendship].asDBObject(hisFriendship))))
  }

  /**
   * @inheritdoc
   */
  def updateFriendship(id: String, friendId: String, status: Option[String], referralStatus: Option[String]): Option[User] = {

    val queryBuilder = MongoDBObject.newBuilder

    status.foreach { status =>
      queryBuilder += "friends.$.status" -> status
    }

    referralStatus.foreach { referralStatus =>
      queryBuilder += "friends.$.referralStatus" -> referralStatus
    }

    findAndModify(
      MongoDBObject(
        "id" -> id,
        "friends.friendId" -> friendId),
      MongoDBObject(
        "$set" -> queryBuilder.result()))
  }

  /**
   * @inheritdoc
   */
  def addFriendship(id: String, friendship: Friendship): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$push" -> MongoDBObject(
          "friends" -> grater[Friendship].asDBObject(friendship))))
  }

  /**
   *
   */
  def updateFriendship(id: String, friendId: String, myStatus: String, friendStatus: String): Option[User] = {
    findAndModify(
      MongoDBObject(
        "id" -> id,
        "friends.friendId" -> friendId),
      MongoDBObject(
        "$set" -> MongoDBObject(
          "friends.$.status" -> myStatus)))

    findAndModify(
      MongoDBObject(
        "id" -> friendId,
        "friends.friendId" -> id),
      MongoDBObject(
        "$set" -> MongoDBObject(
          "friends.$.status" -> friendStatus)))
  }

  /**
   *
   */
  def removeFriendship(id: String, friendId: String): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$pull" -> MongoDBObject(
          "friends" -> MongoDBObject("friendId" -> friendId))))

    findAndModify(
      friendId,
      MongoDBObject(
        "$pull" -> MongoDBObject(
          "friends" -> MongoDBObject("friendId" -> id))))
  }

  /**
   *
   */
  def addMessage(id: String, message: Message): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$push" -> MongoDBObject(
          "profile.messages" -> grater[Message].asDBObject(message))))
  }

  /**
   * @inheritdoc
   */
  def addMessageToEveryone(message: Message): Unit = {
    update(
      query = MongoDBObject(),
      updateRules = MongoDBObject(
        "$push" -> MongoDBObject(
          "profile.messages" -> grater[Message].asDBObject(message))),
      multi = true)
  }

  /**
   *
   */
  def removeOldestMessage(id: String): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$pop" -> MongoDBObject(
          "profile.messages" -> -1)))
  }

  /**
   * @inheritdoc
   */
  def removeMessage(id: String, messageId: String): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$pull" -> MongoDBObject(
          "profile.messages" -> MongoDBObject("id" -> messageId))))
  }

  /**
   *
   */
  def resetTasks(id: String, newTasks: DailyTasks, resetTasksTimeout: Date): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$set" -> MongoDBObject(
          "profile.dailyTasks" -> grater[DailyTasks].asDBObject(newTasks),
          "schedules.dailyTasks" -> resetTasksTimeout)))
  }

  /**
   *
   */
  def addTasks(id: String, newTasks: List[Task], addReward: Option[Assets] = None): Option[User] = {
    val queryBuilder = MongoDBObject.newBuilder

    queryBuilder += ("$push" -> MongoDBObject(
      "profile.dailyTasks.tasks" -> MongoDBObject(
        "$each" -> newTasks.map(grater[Task].asDBObject))))

    addReward match {
      case Some(assets) =>
        queryBuilder += ("$inc" -> MongoDBObject(
          "profile.dailyTasks.reward.coins" -> assets.coins,
          "profile.dailyTasks.reward.money" -> assets.money,
          "profile.dailyTasks.reward.rating" -> assets.rating))
      case _ =>
    }

    findAndModify(
      id,
      queryBuilder.result())
  }

  /**
   * @inheritdoc
   */
  def incTask(id: String, taskId: String): Option[User] = {
    findAndModify(
      MongoDBObject(
        "id" -> id,
        "profile.dailyTasks.tasks.id" -> taskId),
      MongoDBObject(
        "$inc" -> MongoDBObject(
          "profile.dailyTasks.tasks.$.currentCount" -> 1)))
  }

  /**
   * @inheritdoc
   */
  def setTasksCompletedFraction(id: String, completedFraction: Float): Option[User] = {
    findAndModify(
      MongoDBObject(
        "id" -> id),
      MongoDBObject(
      "$set" -> MongoDBObject(
        "profile.dailyTasks.completed" -> completedFraction)))
  }

  /**
   * @inheritdoc
   */
  def setTasksRewardReceived(id: String, rewardReceived: Boolean): Option[User] = {
    findAndModify(
      MongoDBObject(
        "id" -> id),
      MongoDBObject(
        "$set" -> MongoDBObject(
          "profile.dailyTasks.rewardReceived" -> rewardReceived)))
  }

  /**
   *
   */
  def setGender(id: String, gender: String): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$set" -> MongoDBObject(
          "profile.publicProfile.bio.gender" -> gender)))
  }

  /**
   *
   */
  def setDebug(id: String, debug: String): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$set" -> MongoDBObject(
          "profile.debug" -> debug)))
  }

  /**
   *
   */
  def setCity(id: String, city: String): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$set" -> MongoDBObject(
          "profile.publicProfile.bio.city" -> city)))
  }

  def setCountry(id: String, country: String): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$set" -> MongoDBObject(
          "profile.publicProfile.bio.country" -> country)))
  }

  /**
   *
   */
  def addClosedTutorialElement(id: String, platform: String, elementId: String): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$addToSet" -> MongoDBObject(
          s"profile.tutorialStates.$platform.closedElementIds" -> elementId)))
  }

  /**
   *
   */
  def addTutorialTaskAssigned(id: String, platform: String, taskId: String): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$addToSet" -> MongoDBObject(
          s"profile.tutorialStates.$platform.usedTutorialTaskIds" -> taskId)))
  }

  /**
   *
   */
  def addTutorialQuestAssigned(id: String, platform: String, questId: String): Option[User] = {
    findAndModify( // TODO: test me.
      id,
      MongoDBObject(
        "$addToSet" -> MongoDBObject(
          s"profile.tutorialStates.$platform.usedTutorialQuestIds" -> questId)))
  }

  /**
   *
   */
  def updateCultureId(id: String, cultureId: String): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$set" -> MongoDBObject(
          "demo.cultureId" -> cultureId)))
  }

  /**
   *
   */
  def replaceCultureIds(oldCultureId: String, newCultureId: String): Unit = {
    update(
      query = MongoDBObject(
        "demo.cultureId" -> oldCultureId),
      updateRules = MongoDBObject(
        "$set" -> MongoDBObject(
          "demo.cultureId" -> newCultureId)),
      multi = true)
  }

  /**
   * @inheritdoc
   */
  def setTimeLinePopulationTime(id: String, time: Date): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$set" -> MongoDBObject(
          "schedules.timeLine" -> time)))
  }

  /**
   * @inheritdoc
   */
  def addEntryToTimeLine(id: String, entry: TimeLineEntry): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$push" -> MongoDBObject(
          "timeLine" ->
            MongoDBObject(
              "$each" -> List(grater[TimeLineEntry].asDBObject(entry)),
              "$position" -> 0))))
  }

  /**
   * @inheritdoc
   */
  def addEntryToTimeLineMulti(ids: List[String], entry: TimeLineEntry): Unit = {
    update(
      query = MongoDBObject(
        "id" -> MongoDBObject(
          "$in" -> ids)),
      updateRules = MongoDBObject(
        "$push" -> MongoDBObject(
          "timeLine" ->
            MongoDBObject(
              "$each" -> List(grater[TimeLineEntry].asDBObject(entry)),
              "$position" -> 0))),
      multi = true)
  }

  /**
   * @inheritdoc
   */
  def removeEntryFromTimeLineByObjectId(id: String, objectId: String): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$pull" -> MongoDBObject(
          "timeLine" -> MongoDBObject(
          "objectId" -> objectId))))
  }

  /**
   * @inheritdoc
   */
  def addBattleRequest(id: String, battleRequest: BattleRequest): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$push" -> MongoDBObject(
          "battleRequests" -> grater[BattleRequest].asDBObject(battleRequest))))
  }

  /**
   * @inheritdoc
   */
  def updateBattleRequest(id: String, mySolutionId: String, opponentSolutionId: String, status: String): Option[User] = {
    findAndModify(
      MongoDBObject(
        "id" -> id,
        "battleRequests.mySolutionId" -> mySolutionId,
        "battleRequests.opponentSolutionId" -> opponentSolutionId),
      MongoDBObject(
        "$set" -> MongoDBObject(
          "battleRequests.$.status" -> status)))
  }
}

