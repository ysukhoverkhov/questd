package models.store.dao.user

import models.domain.user.User

/**
 * DAO for tutorial related things in user.
 */
trait UserTutorialDAO {

  def addClosedTutorialElement(id: String, platform: String, state: String): Option[User]

  def addTutorialTaskAssigned(id: String, platform: String, taskId: String): Option[User]

  def addTutorialQuestAssigned(id: String, platform: String, questId: String): Option[User]

  def setDailyTasksSuppressed(id: String, platform: String, suppressed: Boolean): Option[User]
}
