package controllers.sn.facebook

import controllers.sn.client.{Invitation, SNUser}
import models.domain.Gender

private[sn] class SNUserFacebook(fbUser: com.restfb.types.User,
                                 client: SocialNetworkClientFacebook,
                                 token: String) extends SNUser {


  private var location: Option[FQLLocation] = None

  /// Name of social network.
  def snName: String = {
    SocialNetworkClientFacebook.Name
  }

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
  def country: String = {
    if (location == None) {
      location = Some(client.fetchLocationFromFB(token))
    }

    // TODO:current_location may be null here, return option and deal with it.
    location.get.current_location.getCountry
  }

  /// City of a user.
  def city: String = {
    if (location == None) {
      location = Some(client.fetchLocationFromFB(token))
    }

    location.get.current_location.getCity
  }

  /**
   * @inheritdoc
   */
  def invitations: List[Invitation] = {
    client.fetchInvitations(token)
  }
}

private[sn] object SNUserFacebook {
  def apply(
      u: com.restfb.types.User,
      c: SocialNetworkClientFacebook,
      t: String): SNUserFacebook = new SNUserFacebook(u, c, t)
}

