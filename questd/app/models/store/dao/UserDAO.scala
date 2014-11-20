package models.store.dao

import models.domain._
import java.util.Date

trait UserDAO extends BaseDAO[User] {

  def updateSessionId(id: String, sessionid: String): Option[User]

  def readBySessionId(sessionid: String): Option[User]
  def readBySNid(snName:String, snid: String): Option[User]

  def addToAssets(id: String, assets: Assets): Option[User]

  def populateMustVoteSolutionsList(userIds: List[String], solutionId: String): Unit
  def removeMustVoteSolution(id: String, solutionId: String): Option[User]

  /**
   * Records vote for quest proposal.
   * @param id Id of user maing a vote.
   * @param questId id of quest we vote for.
   * @param vote our vote.
   * @return Modified user.
   */
  def recordTimeLineVote(id: String, questId: String, vote: ContentVote.Value): Option[User]

  def recordQuestSolving(id: String, questId: String): Option[User]

  /**
   * Updates cool down for inventing quests.
   * @param id If of a user to update for.
   * @param coolDown New cool down date.
   * @return Modified user.
   */
  def updateQuestCreationCoolDown(id: String, coolDown: Date): Option[User]

  def resetPurchases(id: String, resetPurchasesTimeout: Date): Option[User]

  def addPrivateDailyResult(id: String, dailyResult: DailyResult): Option[User]
  def movePrivateDailyResultsToPublic(id: String, dailyResults: List[DailyResult]): Option[User]
  def addQuestIncomeToDailyResult(id: String, questIncome: QuestIncome): Option[User]
  def storeQuestSolvingInDailyResult(id: String, questId: String, reward: Assets): Option[User]
  def storeProposalInDailyResult(id: String, proposal: QuestProposalResult): Option[User]
  def storeSolutionInDailyResult(id: String, solution: QuestSolutionResult): Option[User]

  def levelUp(id: String, ratingToNextLevel: Int): Option[User]
  def setNextLevelRatingAndRights(id: String, newRatingToNextLevel: Int, rights: Rights): Option[User]

  def updateStats(id: String, stats: UserStats): Option[User]

  def addToFollowing(id: String, idToAdd: String): Option[User]
  def removeFromFollowing(id: String, idToRemove: String): Option[User]

  def askFriendship(id: String, idToAdd: String, myFriendship: Friendship, hisFriendship: Friendship): Option[User]
  def updateFriendship(id: String, friendId: String, status: String): Option[User]
  def addFriendship(id: String, friendship: Friendship): Option[User]
  def updateFriendship(id: String, friendId: String, myStatus: String, friendStatus: String): Option[User]
  def removeFriendship(id: String, friendId: String): Option[User]

  def addMessage(id: String, message: Message): Option[User]
  def removeOldestMessage(id: String): Option[User]
  def removeMessage(id: String, messageId: String): Option[User]

  def resetTasks(id: String, newTasks: DailyTasks, resetTasksTimeout: Date): Option[User]
  def addTasks(id: String, newTasks: List[Task], additionalRewaed: Assets): Option[User]
  def incTask(id: String, taskType: String, completed: Float, rewardReceived: Boolean): Option[User]
  def incTutorialTask(id: String, taskId: String, completed: Float, rewardReceived: Boolean): Option[User]

  def updateCultureId(id: String, cultureId: String): Option[User]
  def setGender(id: String, gender: String): Option[User]
  def setDebug(id: String, debug: String): Option[User]
  def setCity(id: String, city: String): Option[User]
  def setCountry(id: String, country: String): Option[User]

  def setTutorialState(id: String, platform: String, state: String): Option[User]
  def addTutorialTaskAssigned(id: String, taskId: String): Option[User]

  def replaceCultureIds(oldCultureId: String, newCultureId: String): Unit

  /**
   * Adds one entry to time line.
   * @param id Id of a user to add to.
   * @param entry Entry to add.
   * @return user after modifications.
   */
  def addEntryToTimeLine(id: String, entry: TimeLineEntry): Option[User]

  /**
   * Adds single time line entry to several users.
   * @param ids Ids of users to add to.
   * @param entry Entry to add.
   */
  def addEntryToTimeLineMulti(ids: List[String], entry: TimeLineEntry): Unit
}
