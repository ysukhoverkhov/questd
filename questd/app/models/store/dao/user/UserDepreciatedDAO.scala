package models.store.dao.user

import models.domain.user.User

/**
 * Depreciated things in user dao.
 */
trait UserDepreciatedDAO {

  def populateMustVoteSolutionsList(userIds: List[String], solutionId: String): Unit

  def removeMustVoteSolution(id: String, solutionId: String): Option[User]
}
