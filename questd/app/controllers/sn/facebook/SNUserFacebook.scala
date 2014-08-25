package controllers.sn.facebook

import controllers.sn.client.SNUser
import models.domain.Gender
import play.Logger

private[sn] class SNUserFacebook(u: com.restfb.types.User, c: SocialNetworkClientFacebook, t: String) extends SNUser {

  private val user = u
  private val client = c
  private val token = t
  
  private var location: Option[FQLLocation] = None

  /// Name of social network.
  def snName: String = {
    SocialNetworkClientFacebook.Name
  }
  
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

  /// Time zone offset of a user.
  def timezone: Int = {
    user.getTimezone().toInt
  }

  /// Country code of a user.
  def country: String = {
    if (location == None) {
      location = Some(c.fetchLocationFromFB(token))
    }
    
    location.get.current_location.getCountry()
  }
  
  /// City of a user.
  def city: String = {
    if (location == None) {
      location = Some(c.fetchLocationFromFB(token))
    }
    
    location.get.current_location.getCity()
  }
}

private[sn] object SNUserFacebook {
  def apply(
      u: com.restfb.types.User, 
      c: SocialNetworkClientFacebook,
      t: String): SNUserFacebook = new SNUserFacebook(u, c, t)
}

