package controllers.sn.facebook

import controllers.sn.client.UserIdInApplication
import controllers.sn.facebook.types.UserIdWithApp

/**
 * Implementation of User id in app for facebook.
 *
 * Created by Yury on 18.09.2014.
 */
private[sn] class UserIdInApplicationFacebook (
  idWithApp: UserIdWithApp
  ) extends ItemFacebook with UserIdInApplication {

  /**
   * @return Identifier of the item in SN.
   */
  def snId = idWithApp.id

  /**
   * @return Name of the application.
   */
  def appName = idWithApp.app.namespace
}

/**
 * Companion object for UserIdInApplicationFacebook class
 */
private[sn] object UserIdInApplicationFacebook {
  def apply(idWithApp: UserIdWithApp): UserIdInApplicationFacebook = new UserIdInApplicationFacebook(idWithApp)
}

