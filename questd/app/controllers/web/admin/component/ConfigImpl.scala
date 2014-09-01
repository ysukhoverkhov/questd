package controllers.web.admin.component

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.ws._
import play.api.libs.json._
import components.APIAccessor
import controllers.domain._
import models.domain.admin._
import controllers.domain.admin._

trait ConfigImpl extends Controller with SecurityAdminImpl { this: APIAccessor =>

  def leftMenu(implicit request: RequestHeader): Map[String, String] = {
    api.getConfiguration(GetConfigurationRequest()) match {
      case OkApiResult(GetConfigurationResult(r)) => r.sections.foldLeft[Map[String, String]](Map()) {
        (c, v) => c + (v.id -> controllers.web.admin.routes.Config.config(v.id).absoluteURL(false))
      }
      case _ => Map()
    }
  }

  def config(sectionName: String) = Authenticated { implicit request =>
    api.getConfigSection(GetConfigSectionRequest(sectionName)) match {
      case OkApiResult(GetConfigSectionResult(Some(r))) => Ok(views.html.admin.config(Menu(request), leftMenu, sectionName, r.values.toSeq.sortBy(_._1)))
      case _ => Ok(views.html.admin.config(Menu(request), leftMenu, sectionName, Map().toSeq))
    }
  }

  def configUpdate(sectionName: String) = Authenticated { implicit request =>

    val values: Map[String, String] = request.body.asFormUrlEncoded match {
      case Some(m) => {
        m.map((e) => e match {
          case (k, v) => (k -> v.head)
        })
      }
      case _ => Map()
    }

    val sec = ConfigSection(sectionName, values)
    api.setConfigSection(SetConfigSectionRequest(sec))

    Redirect(controllers.web.admin.routes.Config.config(sectionName))
  }
}

