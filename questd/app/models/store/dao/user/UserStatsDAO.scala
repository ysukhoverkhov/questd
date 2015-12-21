package models.store.dao.user

import models.domain.common.ContentVote
import models.domain.user.User
import models.domain.user.stats.SolutionsInBattle

/**
 * DAO for things related to user stats.
 */
trait UserStatsDAO {

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
   * Records quest solving and optionally resets bookmark.
   * @param id Id of user solving a quest.
   * @param questId If of a quest to solve
   * @param solutionId Id of solution we solved quest with.
   * @param removeBookmark Should we reset bookmark.
   * @return Modified user.
   */
  def recordQuestSolving(id: String, questId: String, solutionId: String, removeBookmark: Boolean): Option[User]

  /**
   * Sets friends notification flag.
   *
   * @param id our id
   * @param flag new flag state
   */
  def setFriendsNotifiedAboutRegistrationFlag(id: String, flag: Boolean): Option[User]
}
