package controllers.web.admin.component

import controllers.domain.DomainAPIComponent
import controllers.domain.admin.CleanUpObjectsRequest
import controllers.domain.app.user.ResetProfileDebugRequest
import play.api.mvc._

class MaintainsImpl (val api: DomainAPIComponent#DomainAPI) extends Controller with SecurityAdminImpl {

  def cleanup = Authenticated { implicit request =>
    api.cleanUpObjects(CleanUpObjectsRequest())
    Redirect(controllers.web.admin.routes.AdminApp.index())
  }

  def resetProfiles = Authenticated { implicit request =>

    api.db.user.all.foreach { user =>
      api.resetProfileDebug(ResetProfileDebugRequest(user))
    }

    Redirect(controllers.web.admin.routes.AdminApp.index())
  }


}

