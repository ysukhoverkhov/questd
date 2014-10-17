package models.store.mongo.dao

import java.util.Date

import com.mongodb.casbah.commons._
import com.novus.salat._
import models.domain._
import models.domain.view._
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
  def selectQuestSolutionVote(
    id: String,
    qsi: QuestSolutionInfoWithID,
    qsa: PublicProfileWithID,
    qi: QuestInfoWithID): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$set" -> MongoDBObject(
          "profile.questSolutionVoteContext.reviewingQuestSolution" -> grater[QuestSolutionInfoWithID].asDBObject(qsi),
          "profile.questSolutionVoteContext.authorOfQuestSolution" -> grater[PublicProfileWithID].asDBObject(qsa),
          "profile.questSolutionVoteContext.questOfSolution" -> grater[QuestInfoWithID].asDBObject(qi))))
  }

  /**
   *
   */
  def recordQuestSolutionVote(id: String, solutionId: String): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$inc" -> MongoDBObject(
          "profile.questSolutionVoteContext.numberOfReviewedSolutions" -> 1),
        "$unset" -> MongoDBObject(
          "profile.questSolutionVoteContext.reviewingQuestSolution" -> "",
          "profile.questSolutionVoteContext.authorOfQuestSolution" -> "",
          "profile.questSolutionVoteContext.questOfSolution" -> ""),
        "$push" -> MongoDBObject(
          "history.votedQuestSolutionIds.0" -> solutionId)))
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
   *
   */
  def selectQuestProposalVote(id: String, questInfo: QuestInfoWithID, themeInfo: ThemeInfoWithID): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$set" -> MongoDBObject(
          "profile.questProposalVoteContext.reviewingQuest" -> grater[QuestInfoWithID].asDBObject(questInfo),
          "profile.questProposalVoteContext.themeOfQuest" -> grater[ThemeInfoWithID].asDBObject(themeInfo)),
        "$inc" -> MongoDBObject(
          "stats.proposalsVoted" -> 1)))
  }

  /**
   *
   */
  def recordQuestProposalVote(id: String, questId: String, liked: Boolean): Option[User] = {
    val queryBuilder = MongoDBObject.newBuilder

    if (liked) {
      queryBuilder += ("$push" -> MongoDBObject(
        "history.votedQuestProposalIds.0" -> questId,
        "history.likedQuestProposalIds.0" -> questId))
    } else {
      queryBuilder += ("$push" -> MongoDBObject(
        "history.votedQuestProposalIds.0" -> questId))
    }

    queryBuilder += ("$inc" -> MongoDBObject(
      "profile.questProposalVoteContext.numberOfReviewedQuests" -> 1,
      "stats.proposalsLiked" -> (if (liked) 1 else 0)))

    queryBuilder += ("$unset" -> MongoDBObject(
      "profile.questProposalVoteContext.reviewingQuest" -> "",
      "profile.questProposalVoteContext.themeOfQuest" -> ""))

    findAndModify(
      id,
      queryBuilder.result())
  }

  /**
   *
   */
  def purchaseQuest(id: String, purchasedQuest: QuestInfoWithID, author: PublicProfileWithID, defeatReward: Assets, victoryReward: Assets): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$set" -> MongoDBObject(
          "profile.questSolutionContext.purchasedQuest" -> grater[QuestInfoWithID].asDBObject(purchasedQuest),
          "profile.questSolutionContext.questAuthor" -> grater[PublicProfileWithID].asDBObject(author),
          "profile.questSolutionContext.defeatReward" -> grater[Assets].asDBObject(defeatReward),
          "profile.questSolutionContext.victoryReward" -> grater[Assets].asDBObject(victoryReward)),
        "$inc" -> MongoDBObject(
          "profile.questSolutionContext.numberOfPurchasedQuests" -> 1,
          "stats.questsReviewed" -> 1),
        "$push" -> MongoDBObject(
          "history.solvedQuestIds.0" -> purchasedQuest.id)))
  }

  /**
   *
   */
  def takeQuest(id: String, takenQuest: QuestInfoWithID, cooldown: Date, deadline: Date): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$set" -> MongoDBObject(
          "profile.questSolutionContext.numberOfPurchasedQuests" -> 0,
          "profile.questSolutionContext.takenQuest" -> grater[QuestInfoWithID].asDBObject(takenQuest),
          "profile.questSolutionContext.questCooldown" -> cooldown,
          "profile.questSolutionContext.questDeadline" -> deadline),
        "$unset" -> MongoDBObject(
          "profile.questSolutionContext.purchasedQuest" -> ""),
        "$inc" -> MongoDBObject(
          "stats.questsAccepted" -> 1)
//        ,
//        "$addToSet" -> MongoDBObject(
//          "history.themesOfSelectedQuests" -> takenQuest.obj.themeId))
    ))
  }

  /**
   *
   */
  def resetQuestSolution(id: String, shouldResetCooldown: Boolean): Option[User] = {

    val queryBuilder = MongoDBObject.newBuilder

    queryBuilder += ("$set" -> MongoDBObject(
      "profile.questSolutionContext.numberOfPurchasedQuests" -> 0))

    if (shouldResetCooldown) {
      queryBuilder += ("$unset" -> MongoDBObject(
        "profile.questSolutionContext.purchasedQuest" -> "",
        "profile.questSolutionContext.takenQuest" -> "",
        "profile.questSolutionContext.questAuthor" -> "",
        "profile.questSolutionContext.questCooldown" -> ""))
    } else {
      queryBuilder += ("$unset" -> MongoDBObject(
        "profile.questSolutionContext.purchasedQuest" -> "",
        "profile.questSolutionContext.takenQuest" -> "",
        "profile.questSolutionContext.questAuthor" -> ""))
    }

    findAndModify(
      id,
      queryBuilder.result())
  }

  /**
   *
   */
  // TODO: test me.
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
  // TODO: check everything is in the place (everything is present in model).
  def resetPurchases(id: String, resetPurchasesTimeout: Date): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$set" -> MongoDBObject(
          "profile.questSolutionContext.numberOfPurchasedQuests" -> 0,
          "profile.questProposalContext.numberOfPurchasedThemes" -> 0,
          "profile.questProposalVoteContext.numberOfReviewedQuests" -> 0,
          "profile.questSolutionVoteContext.numberOfReviewedSolutions" -> 0,
          "schedules.purchases" -> resetPurchasesTimeout),
        "$unset" -> MongoDBObject(
          "profile.questSolutionContext.purchasedQuest" -> "",
          "profile.questProposalContext.purchasedTheme" -> "",
          "profile.questProposalContext.todayReviewedThemeIds" -> "",
          "profile.questProposalVoteContext.reviewingQuest" -> "",
          "profile.questSolutionVoteContext.reviewingQuestSolution" -> "")))
  }

  /**
   *
   */
  def resetTodayReviewedThemes(id: String): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$unset" -> MongoDBObject(
          "profile.questProposalContext.todayReviewedThemeIds" -> "")))
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
  def storeProposalOutOfTimePenalty(id: String, penalty: Assets): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$set" -> MongoDBObject(
          "privateDailyResults.0.proposalGiveUpAssetsDecrease" -> grater[Assets].asDBObject(penalty))))
  }

  /**
   *
   */
  def storeSolutionOutOfTimePenalty(id: String, penalty: Assets): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$set" -> MongoDBObject(
          "privateDailyResults.0.questGiveUpAssetsDecrease" -> grater[Assets].asDBObject(penalty))))
  }

  /**
   *
   */
  def levelup(id: String, ratingToNextlevel: Int): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$inc" -> MongoDBObject(
          "profile.publicProfile.level" -> 1,
          "profile.assets.rating" -> -ratingToNextlevel)))
  }

  /**
   *
   */
  def setNextLevelRatingAndRights(id: String, newRatingToNextlevel: Int, rights: Rights): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$set" -> MongoDBObject(
          "profile.ratingToNextLevel" -> newRatingToNextlevel,
          "profile.rights" -> grater[Rights].asDBObject(rights))))
  }

  /**
   *
   */
  def addFreshDayToHistory(id: String): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$push" -> MongoDBObject(
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
              "$position" -> 0))))
  }

  /**
   *
   */
  def removeLastDayFromHistory(id: String): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$pop" -> MongoDBObject(
          "history.votedQuestProposalIds" -> 1,
          "history.solvedQuestIds" -> 1,
          "history.votedQuestSolutionIds" -> 1,
          "history.likedQuestProposalIds" -> 1)))
  }

  /**
   *
   */
  def removeLastThemesFromHistory(id: String, themesToRemove: Int): Option[User] = {
    (1 to themesToRemove) map { a: Int =>
      findAndModify(
        id,
        MongoDBObject(
          "$pop" -> MongoDBObject(
            "history.selectedThemeIds" -> -1)))
    } reduce { (r, c) =>
      r
    }
  }

  /**
   *
   */
  def removeLastQuestThemesFromHistory(id: String, themesToRemove: Int): Option[User] = {
    (1 to themesToRemove) map { a: Int =>
      findAndModify(
        id,
        MongoDBObject(
          "$pop" -> MongoDBObject(
            "history.themesOfSelectedQuests" -> -1)))
    } reduce { (r, c) =>
      r
    }
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
}

