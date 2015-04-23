package controllers.web.admin.component

import controllers.domain.DomainAPIComponent
import models.domain.TutorialPlatform
import play.api.mvc._

class TutorialScriptsCRUDImpl (val api: DomainAPIComponent#DomainAPI) extends Controller with SecurityAdminImpl {

  private def leftMenu(implicit request: RequestHeader): Map[String, String] = {
    TutorialPlatform.values.foldLeft[Map[String, String]](Map.empty) {
      (c, v) => c + (v.toString -> controllers.web.admin.routes.TutorialScriptsCRUD.tutorial(v.toString).absoluteURL(secure = false))
    }
  }

  def tutorial(platform: String) = Authenticated { implicit request =>
//    api.getConfigSection(GetConfigSectionRequest(sectionName)) match {
//      case OkApiResult(GetConfigSectionResult(Some(r))) => Ok(views.html.admin.config(Menu(request), leftMenu, sectionName, r.values.toSeq.sortBy(_._1)))
//      case _ => Ok(views.html.admin.config(Menu(request), leftMenu, sectionName, Map.empty.toSeq))
//    }
    Ok(views.html.admin.tutorialScripts(Menu(request), leftMenu, platform))
  }

//  def configUpdate(sectionName: String) = Authenticated { implicit request =>
//
//    val values: Map[String, String] = request.body.asFormUrlEncoded match {
//      case Some(m) =>
//        m.map {
//          case (k, v) => k -> v.head
//        }
//      case _ => Map.empty
//    }
//
//    val sec = ConfigSection(sectionName, values)
//    api.setConfigSection(SetConfigSectionRequest(sec))
//
//    Redirect(controllers.web.admin.routes.Config.config(sectionName))
//  }
}

