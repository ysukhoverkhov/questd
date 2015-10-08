package controllers.services.socialnetworks.facebook

import controllers.services.socialnetworks.client.Permission
import controllers.services.socialnetworks.facebook.types.AppPermission

/**
 * Mapper to fb permissions.
 */
private[facebook] object PermissionMapper {
  def apply(appPermission: AppPermission): Permission.Value = {
    if (appPermission.status == "granted") {
      appPermission.permission match {
        case "user_friends" => Permission.Friends
        case _ => Permission.Invalid
      }
    } else {
      Permission.Invalid
    }
  }
}
