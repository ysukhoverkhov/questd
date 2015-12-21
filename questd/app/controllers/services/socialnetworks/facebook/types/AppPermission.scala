package controllers.services.socialnetworks.facebook.types

import com.restfb.Facebook

/**
 * Facebook type for permissions user gave to the app.
 */
private[facebook] class AppPermission {
  @Facebook
  var permission: String = _

  @Facebook
  var status: String = _
}
