package controllers.sn.facebook.types

import com.restfb.Facebook

/**
 * Facebook type for application.
 */
private[facebook] class App {
  @Facebook
  var name: String = _

  @Facebook
  var namespace: String = _

  @Facebook
  var id: String = _
}
