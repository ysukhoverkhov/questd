package models.store.dao.user

import models.domain.user.User
import models.domain.user.auth.CrossPromotedApp

/**
 * DAO related to user auth.
 */
trait UserAuthDAO {

  def updateSessionId(id: String, sessionId: String): Option[User]

  /**
   * Adds one or some cross promoted apps info.
   * @param id Id of a user to modify
   * @param snName name of network to modify.
   * @param apps List of apps to add.
   */
  def addCrossPromotions(id: String, snName: String, apps: List[CrossPromotedApp]): Option[User]
}
