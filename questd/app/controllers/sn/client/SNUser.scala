package controllers.sn.client

import models.domain.Gender

trait SNUser {

  /// Id of user in terms of social network.
  def snId: String
  
  /// First name of our user.
  def firstName: String
  
  /// Gender of a user.
  def gender: Gender.Value

  /// Time zone ofset of a user.
  def timezone: Int
}
