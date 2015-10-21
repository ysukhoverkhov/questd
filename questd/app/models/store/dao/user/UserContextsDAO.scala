package models.store.dao.user

import java.util.Date

import models.domain.user.User
import models.view.QuestView

/**
 * DAO related to updating different contexts of a user.
 */
trait UserContextsDAO {

  /**
   * Set quest bookmark for a user.
   * @param id Id of a user setting a bookmark.
   * @param questId Id of a quest set bookmark.
   * @return Modified user.
   */
  def setQuestBookmark(id: String, questId: QuestView): Option[User]

  /**
   * Updates cool down for inventing quests.
   * @param id If of a user to update for.
   * @param coolDown New cool down date.
   * @return Modified user.
   */
  def updateQuestCreationCoolDown(id: String, coolDown: Date): Option[User]
}
