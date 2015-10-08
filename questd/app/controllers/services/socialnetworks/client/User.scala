package controllers.services.socialnetworks.client

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
   * @return friends of a user who plays the game as well.
   */
  def friends: List[User]

  /**
   * All user invitations.
   * @return All current invitations
   */
  def invitations: List[Invitation]

  /**
   * @return List of the user ids in all other applications.
   */
  def idsInOtherApps: List[UserIdInApplication]

  /**
   * @return Permissions user gave to the app.
   */
  def permissions: List[Permission.Value]
  }

