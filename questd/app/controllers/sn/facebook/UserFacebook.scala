package controllers.sn.facebook

import controllers.sn.client.{Invitation, User}
import models.domain.Gender

private[sn] class UserFacebook(fbUser: com.restfb.types.User,
                               client: SocialNetworkClientFacebook,
                               token: String) extends ItemFacebook with User {


  private var location: Option[FQLLocation] = None

  /// Id of user in terms of social network.
  def snId: String = {
    fbUser.getId
  }

  /// First name of our user.
  def firstName: String = {
    fbUser.getFirstName
  }

  /// Gender of a user.
  def gender: Gender.Value = {
    fbUser.getGender match {
      case "male" => Gender.Male
      case "female" => Gender.Female
      case _ => Gender.Unknown
    }
  }

  /// Time zone offset of a user.
  def timezone: Int = {
    fbUser.getTimezone.toInt
  }

  /// Country code of a user.
  def country: Option[String] = {
    if (location == None) {
      location = Some(client.fetchLocationFromFB(token))
    }

    if (location.get.current_location == null)
      None
    else
      Some(location.get.current_location.getCountry)
  }

  /// City of a user.
  def city: Option[String] = {
    if (location == None) {
      location = Some(client.fetchLocationFromFB(token))
    }

    if (location.get.current_location == null)
      None
    else
      Some(location.get.current_location.getCity)
  }

  /**
   * @inheritdoc
   */
  def invitations: List[Invitation] = {
    client.fetchInvitations(token)
  }
}

private[sn] object UserFacebook {
  def apply(
      u: com.restfb.types.User,
      c: SocialNetworkClientFacebook,
      t: String): UserFacebook = new UserFacebook(u, c, t)
}

