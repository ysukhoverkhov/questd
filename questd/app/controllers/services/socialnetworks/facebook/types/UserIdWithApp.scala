package controllers.services.socialnetworks.facebook.types

import com.restfb.Facebook

/**
 * Facebook type for user id in app what is part of a business
 */
private[facebook] class UserIdWithApp {
  @Facebook
  var id: String = _

  @Facebook
  var app: App = _
}
