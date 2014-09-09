package controllers.web.admin.component

import controllers.domain.{OkApiResult, DomainAPIComponent}
import controllers.domain.admin._
import play.api.mvc._
import models.domain.admin._

class ConfigImpl (val api: DomainAPIComponent#DomainAPI) extends Controller with SecurityAdminImpl {

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
      case Some(m) =>
        m.map {
          case (k, v) => k -> v.head
        }
      case _ => Map()
    }

    val sec = ConfigSection(sectionName, values)
    api.setConfigSection(SetConfigSectionRequest(sec))

    Redirect(controllers.web.admin.routes.Config.config(sectionName))
  }
}

