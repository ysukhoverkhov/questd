package controllers.web.admin.component

import controllers.domain.admin.{AllUsersRequest, AllUsersResult}
import controllers.domain.{DomainAPIComponent, OkApiResult}
import play.api.mvc._

class UsersCRUDImpl(val api: DomainAPIComponent#DomainAPI) extends Controller with SecurityAdminImpl {

  /**
   * Get all users action
   */
  def users(id: String) = Authenticated { implicit request =>

    // Filling table.
    api.allUsers(AllUsersRequest()) match {

      case OkApiResult(a: AllUsersResult) => Ok(
        views.html.admin.users(
          Menu(request),
          a.users.toList))

      case _ => Ok("Internal server error - themes not received.")
    }
  }

}

