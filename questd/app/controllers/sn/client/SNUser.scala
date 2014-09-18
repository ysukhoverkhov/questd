package controllers.sn.client

import models.domain.Gender

trait SNUser {

  /// Name of social network.
  def snName: String

  /// Id of user in terms of social network.
  def snId: String

  /// First name of our user.
  def firstName: String

  /// Gender of a user.
  def gender: Gender.Value

  /// Time zone offset of a user.
  def timezone: Int

  /// Country of a user.
  def country: String

  /// City of a user.
  def city: String

  /**
   * All user invitations.
   * @return All current invitations
   */
  def invitations: List[Invitation]
}
