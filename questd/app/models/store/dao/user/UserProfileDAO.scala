package models.store.dao.user

import models.domain.common.Assets
import models.domain.user.User
import models.domain.user.profile.Rights

/**
 * DAO related to user profile.
 */
trait UserProfileDAO {

  def addToAssets(id: String, assets: Assets): Option[User]

  def levelUp(id: String, ratingToNextLevel: Int): Option[User]

  def setNextLevelRatingAndRights(id: String, newRatingToNextLevel: Int, rights: Rights): Option[User]

  def updateCultureId(id: String, cultureId: String): Option[User]

  def setGender(id: String, gender: String): Option[User]

  def setDebug(id: String, debug: String): Option[User]

  def setCity(id: String, city: String): Option[User]

  def setCountry(id: String, country: String): Option[User]

  def replaceCultureIds(oldCultureId: String, newCultureId: String): Unit

  /**
   * Set user source for analytics
   *
   * @param id Id if user to set source to.
   * @param userSource Sourece of a user.
   */
  def setUserSource(id: String, userSource: Map[String, String]): Option[User]
}
