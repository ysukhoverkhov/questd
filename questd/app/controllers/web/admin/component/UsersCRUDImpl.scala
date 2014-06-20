package controllers.web.admin.component

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.ws._
import play.api.libs.json._

import models.domain._
import controllers.domain._
import controllers.domain.admin._
import components._

trait UsersCRUDImpl extends Controller { this: APIAccessor =>

  /**
   * Get all users action
   */
  def users(id: String) = Action { implicit request =>

    // Filling table.
    api.allUsers(AllUsersRequest()) match {

      case OkApiResult(Some(a: AllUsersResult)) => Ok(
        views.html.admin.users(
          Menu(request),
          a.users.toList))

      case _ => Ok("Internal server error - themes not received.")
    }
  }

}
