package controllers.sn.facebook

import controllers.sn.client.SNUser
import models.domain.Gender

private[sn] class SNUserFacebook(u: com.restfb.types.User) extends SNUser {

  private val user = u

  /// Id of user in terms of social network.
  def snId: String = {
    u.getId()
  }
  
  /// First name of our user.
  def firstName: String = {
    u.getFirstName()
  }
  
  /// Gender of a user.
  def gender: Gender.Value = {
    (user.getGender()) match {
      case "male" => Gender.Male
      case "female" => Gender.Female
      case _ => Gender.Unknown
    }
  }

  /// Time zone ofset of a user.
  def timezone: Int = {
    user.getTimezone().toInt
  }
}

private[sn] object SNUserFacebook {
  def apply(u: com.restfb.types.User): SNUserFacebook = new SNUserFacebook(u)
}

