package models.store.dao

import java.util.Date

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
import models.view.QuestView

trait UserDAO extends BaseDAO[User] {

  def updateSessionId(id: String, sessionId: String): Option[User]

  /**
   * Adds one or some cross promoted apps info.
   * @param id Id of a user to modify
   * @param snName name of network to modify.
   * @param apps List of apps to add.
   */
  def addCrossPromotions(id: String, snName: String, apps: List[CrossPromotedApp]): Option[User]

  def readBySessionId(sessionid: String): Option[User]
  def readBySNid(snName:String, snid: String): Option[User]

  def addToAssets(id: String, assets: Assets): Option[User]

  def populateMustVoteSolutionsList(userIds: List[String], solutionId: String): Unit
  def removeMustVoteSolution(id: String, solutionId: String): Option[User]

  /**
   * Records quest creation.
   * @param id Id of user creating a quest.
   * @param questId Id of a created quest.
   * @return Modified user.
   */
  def recordQuestCreation(id: String, questId: String): Option[User]

  /**
   * Records vote for quest proposal.
   * @param id Id of user making a vote.
   * @param questId id of quest we vote for.
   * @param vote our vote.
   * @return Modified user.
   */
  def recordQuestVote(id: String, questId: String, vote: ContentVote.Value): Option[User]

  /**
   * Records vote for quest proposal.
   * @param id Id of user making a vote.
   * @param solutionId id of solution we vote for.
   * @param vote our vote.
   * @return Modified user.
   */
  def recordSolutionVote(id: String, solutionId: String, vote: ContentVote.Value): Option[User]

  /**
   * Record vote of battle.
   *
   * @param id Id of user to record vote.
   * @param battleId Battle id we voted.
   * @param solutionId Solution we voted for.
   * @return Updated user.
   */
  def recordBattleVote(id: String, battleId: String, solutionId: String): Option[User]

  /**
   * Records a battle user participated in.
   *
   * @param id Id of user.
   * @param battleId Id of battle
   * @param rivalSolutionIds Our rival in battle
   * @return
   */
  def recordBattleParticipation(id: String, battleId: String, rivalSolutionIds: SolutionsInBattle): Option[User]

  /**
   * Set quest bookmark for a user.
   * @param id Id of a user setting a bookmark.
   * @param questId Id of a quest set bookmark.
   * @return Modified user.
   */
  def setQuestBookmark(id: String, questId: QuestView): Option[User]

  /**
   * Records quest solving and optionally resets bookmark.
   * @param id Id of user solving a quest.
   * @param questId If of a quest to solve
   * @param solutionId Id of solution we solved quest with.
   * @param removeBookmark Should we reset bookmark.
   * @return Modified user.
   */
  def recordQuestSolving(id: String, questId: String, solutionId: String, removeBookmark: Boolean): Option[User]

  /**
   * Updates cool down for inventing quests.
   * @param id If of a user to update for.
   * @param coolDown New cool down date.
   * @return Modified user.
   */
  def updateQuestCreationCoolDown(id: String, coolDown: Date): Option[User]

  def addPrivateDailyResult(id: String, dailyResult: DailyResult): Option[User]
  def movePrivateDailyResultsToPublic(id: String, dailyResults: List[DailyResult]): Option[User]
  def addQuestIncomeToDailyResult(id: String, questIncome: QuestIncome): Option[User]
  def removeQuestIncomeFromDailyResult(id: String, questId: String): Option[User]
  def storeQuestSolvingInDailyResult(id: String, questId: String, reward: Assets): Option[User]
  def storeQuestInDailyResult(id: String, proposal: QuestResult): Option[User]
  def storeSolutionInDailyResult(id: String, solution: SolutionResult): Option[User]
  def storeBattleInDailyResult(id: String, battle: BattleResult): Option[User]

  def levelUp(id: String, ratingToNextLevel: Int): Option[User]
  def setNextLevelRatingAndRights(id: String, newRatingToNextLevel: Int, rights: Rights): Option[User]

  def addToFollowing(id: String, idToAdd: String): Option[User]
  def removeFromFollowing(id: String, idToRemove: String): Option[User]

  def askFriendship(id: String, idToAdd: String, myFriendship: Friendship, hisFriendship: Friendship): Option[User]
  def updateFriendship(id: String, friendId: String, status: Option[String], referralStatus: Option[String]): Option[User]
  def updateFriendship(id: String, friendId: String, myStatus: String, friendStatus: String): Option[User]
  def addFriendship(id: String, friendship: Friendship): Option[User]
  def removeFriendship(id: String, friendId: String): Option[User]

  def addMessage(id: String, message: Message): Option[User]
  def addMessageToEveryone(message: Message): Unit
  def removeOldestMessage(id: String): Option[User]
  def removeMessage(id: String, messageId: String): Option[User]

  def resetTasks(id: String, newTasks: DailyTasks, resetTasksTimeout: Date): Option[User]
  def addTasks(id: String, newTasks: List[Task], addReward: Option[Assets] = None): Option[User]
  def incTask(id: String, taskId: String): Option[User]
  def setTasksCompletedFraction(id: String, completedFraction: Float): Option[User]
  def setTasksRewardReceived(id: String, rewardReceived: Boolean): Option[User]

  def updateCultureId(id: String, cultureId: String): Option[User]
  def setGender(id: String, gender: String): Option[User]
  def setDebug(id: String, debug: String): Option[User]
  def setCity(id: String, city: String): Option[User]
  def setCountry(id: String, country: String): Option[User]

  def addClosedTutorialElement(id: String, platform: String, state: String): Option[User]
  def addTutorialTaskAssigned(id: String, platform: String, taskId: String): Option[User]
  def addTutorialQuestAssigned(id: String, platform: String, questId: String): Option[User]
  def setRequestForTutorialBattlesUsed(id: String, platform: String, used: Boolean): Option[User]

  def replaceCultureIds(oldCultureId: String, newCultureId: String): Unit


  /**
   * Set new time to populate time line at.
   * @param id id of a user to set time.
   * @param time New time.
   * @return Updated user.
   */
  def setTimeLinePopulationTime(id: String, time: Date): Option[User]

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

  /**
   * Removes entry from time line.
   * @param id Id of a user to add to.
   * @param entryId Entry to remove.
   * @return user after modifications.
   */
  def removeEntryFromTimeLineByObjectId(id: String, entryId: String): Option[User]

  /**
   * Adds battle request to user.
   *
   * @param id Id of user to add request to.
   * @param battleRequest Request to add.
   * @return Modified user.
   */
  def addBattleRequest(id: String, battleRequest: BattleRequest): Option[User]

  /**
   * Updates status of batle request.
   *
   * @param id Id of user to update request for.
   * @param mySolutionId Id of user's challenged solution.
   * @param opponentSolutionId Id of opponent's sopution.
   * @param status new status.
   */
  def updateBattleRequest(id: String, mySolutionId: String, opponentSolutionId: String, status: String): Option[User]
}
