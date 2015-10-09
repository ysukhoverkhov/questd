package models.store.dao.user

import java.util.Date

import models.domain.common.Assets
import models.domain.user.User
import models.domain.user.profile.{DailyTasks, Task}

/**
 * DAO for task related things in user DAO
 *
 */
trait UserTasksDAO {

  def resetTasks(id: String, newTasks: DailyTasks, resetTasksTimeout: Date): Option[User]

  def addTasks(id: String, newTasks: List[Task], addReward: Option[Assets] = None): Option[User]

  def incTask(id: String, taskId: String): Option[User]

  def setTasksCompletedFraction(id: String, completedFraction: Float): Option[User]

  def setTasksRewardReceived(id: String, rewardReceived: Boolean): Option[User]
}
