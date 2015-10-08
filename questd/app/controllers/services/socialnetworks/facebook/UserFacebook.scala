package controllers.services.socialnetworks.facebook

import controllers.services.socialnetworks.client.{Permission, UserIdInApplication, Invitation, User}
import models.domain.user.profile.Gender

private[socialnetworks] class UserFacebook(
  fbUser: com.restfb.types.User,
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

  /// Updating location cache
  private def checkUpdateUserLocation(): Unit = {
  }

  /// Country code of a user.
  def country: Option[String] = {
    checkUpdateUserLocation()
    None
  }

  /// City of a user.
  def city: Option[String] = {
    checkUpdateUserLocation()
    None
  }

  /**
   * @inheritdoc
   */
  def friends: List[User] = {
    client.fetchFriendsByToken(token)
  }

  /**
   * @inheritdoc
   */
  def invitations: List[Invitation] = {
    client.fetchInvitations(token)
  }

  /**
   * @inheritdoc
   */
  def idsInOtherApps: List[UserIdInApplication] = {
    client.fetchIdsInOtherApps(token)
  }

  /**
   * @inheritdoc
   */
  def permissions: List[Permission.Value] = {
    client.fetchPermissions(token)
  }

}

private[socialnetworks] object UserFacebook {
  def apply(
      u: com.restfb.types.User,
      c: SocialNetworkClientFacebook,
      t: String): UserFacebook = new UserFacebook(u, c, t)
}

