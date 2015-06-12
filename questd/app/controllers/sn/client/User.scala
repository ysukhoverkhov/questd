package controllers.sn.client

import models.domain.user.profile.Gender

trait User extends Item {

  /// First name of our user.
  def firstName: String

  /// Gender of a user.
  def gender: Gender.Value

  /// Time zone offset of a user.
  def timezone: Int

  /// Country of a user.
  def country: Option[String]

  /// City of a user.
  def city: Option[String]

  /**
   * All user invitations.
   * @return All current invitations
   */
  def invitations: List[Invitation]

  /**
   * @return List of the user ids in all other applications.
   */
  def idsInOtherApps: List[UserIdInApplication]
}

