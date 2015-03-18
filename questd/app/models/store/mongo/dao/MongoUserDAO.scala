package models.store.mongo.dao

import java.util.Date

import com.mongodb.casbah.commons._
import com.novus.salat._
import models.domain._
import models.domain.view.QuestView
import models.store.dao._
import models.store.mongo.helpers._
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
  def readBySessionId(sessid: String): Option[User] = {
    readByExample("auth.session", sessid)
  }

  /**
   * Read by fb id
   */
  def readBySNid(snName: String, snid: String): Option[User] = {
    readByExample(s"auth.snids.$snName", snid)
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
   *
   */
  def populateMustVoteSolutionsList(userIds: List[String], solutionId: String): Unit = {
    update(
      query = MongoDBObject(
        "id" -> MongoDBObject(
          "$in" -> userIds
        )),
      u = MongoDBObject(
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
  def recordSolutionCreation(id: String, solutionId: String): Option[User] = {
    val queryBuilder = MongoDBObject.newBuilder

    queryBuilder += ("$push" -> MongoDBObject(
      "stats.createdSolutions" -> solutionId))

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
  def recordQuestSolving(id: String, questId: String, removeBookmark: Boolean): Option[User] = {

    val queryBuilder = MongoDBObject.newBuilder

    if (removeBookmark) {
      queryBuilder += ("$unset" -> MongoDBObject(
        "profile.questSolutionContext.bookmarkedQuest" -> ""))
    }

    queryBuilder += ("$push" -> MongoDBObject(
      "stats.solvedQuests" -> questId))

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
  def storeProposalInDailyResult(id: String, proposal: QuestProposalResult): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$push" -> MongoDBObject(
          "privateDailyResults.0.decidedQuestProposals" -> grater[QuestProposalResult].asDBObject(proposal))))
  }

  /**
   *
   */
  def storeSolutionInDailyResult(id: String, solution: QuestSolutionResult): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$push" -> MongoDBObject(
          "privateDailyResults.0.decidedQuestSolutions" -> grater[QuestSolutionResult].asDBObject(solution))))
  }

  /**
   *
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
  def updateStats(id: String, stats: UserStats): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$set" -> MongoDBObject(
          "stats" -> grater[UserStats].asDBObject(stats))))
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
  def updateFriendship(id: String, friendId: String, status: String): Option[User] = {
    findAndModify(
      MongoDBObject(
        "id" -> id,
        "friends.friendId" -> friendId),
      MongoDBObject(
        "$set" -> MongoDBObject(
          "friends.$.status" -> status)))
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
          "messages" -> grater[Message].asDBObject(message))))
  }

  /**
   *
   */
  def removeOldestMessage(id: String): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$pop" -> MongoDBObject(
          "messages" -> -1)))
  }

  /**
   *
   */
  def removeMessage(id: String, messageId: String): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$pull" -> MongoDBObject(
          "messages" -> MongoDBObject("id" -> messageId))))
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
  def addTasks(id: String, newTasks: List[Task], additionalReward: Assets): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$inc" -> MongoDBObject(
          "profile.dailyTasks.reward.coins" -> additionalReward.coins,
          "profile.dailyTasks.reward.money" -> additionalReward.money,
          "profile.dailyTasks.reward.rating" -> additionalReward.rating),
        "$push" -> MongoDBObject(
          "profile.dailyTasks.tasks" -> MongoDBObject(
            "$each" -> newTasks.map(grater[Task].asDBObject)))))
  }

  /**
   *
   */
  def incTask(id: String, taskType: String, completed: Float, rewardReceived: Boolean): Option[User] = {
    findAndModify(
      MongoDBObject(
        "id" -> id,
        "profile.dailyTasks.tasks.taskType" -> taskType),
      MongoDBObject(
        "$inc" -> MongoDBObject(
          "profile.dailyTasks.tasks.$.currentCount" -> 1),
        "$set" -> MongoDBObject(
          "profile.dailyTasks.completed" -> completed,
          "profile.dailyTasks.rewardReceived" -> rewardReceived)))
  }

  /**
   *
   */
  def incTutorialTask(id: String, taskId: String, completed: Float, rewardReceived: Boolean): Option[User] = {
    findAndModify(
      MongoDBObject(
        "id" -> id,
        "profile.dailyTasks.tasks.tutorialTask.id" -> taskId),
      MongoDBObject(
        "$inc" -> MongoDBObject(
          "profile.dailyTasks.tasks.$.currentCount" -> 1),
        "$set" -> MongoDBObject(
          "profile.dailyTasks.completed" -> completed,
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
  def setTutorialState(id: String, platform: String, state: String): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$set" -> MongoDBObject(
          s"tutorial.clientTutorialState.$platform" -> state)))
  }

  /**
   *
   */
  def addTutorialTaskAssigned(id: String, taskId: String): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$addToSet" -> MongoDBObject(
          "tutorial.assignedTutorialTaskIds" -> taskId)))
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
      u = MongoDBObject(
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
      u = MongoDBObject(
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
}

