package controllers.web.admin.component

import controllers.domain.DomainAPIComponent
import controllers.domain.admin.{ExportAnalyticsRequest, CleanUpObjectsRequest}
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

  def exportAnalytics = Authenticated { implicit request =>
    val result = api.exportAnalytics(ExportAnalyticsRequest())

    Ok(result.body.get.data).withHeaders(CACHE_CONTROL -> "max-age=0", CONTENT_DISPOSITION -> s"attachment; filename=ana.csv", CONTENT_TYPE -> "application/x-download")
  }

}

