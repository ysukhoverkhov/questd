package controllers.web.admin.component

import controllers.domain.app.user.{GetCommonTutorialRequest, GetCommonTutorialResult}
import controllers.domain.{DomainAPIComponent, OkApiResult}
import models.domain._
import play.api.mvc._

class TutorialScriptsCRUDImpl (val api: DomainAPIComponent#DomainAPI) extends Controller with SecurityAdminImpl {

  private def leftMenu(implicit request: RequestHeader): Map[String, String] = {
    TutorialPlatform.values.foldLeft[Map[String, String]](Map.empty) {
      (c, v) => c + (v.toString -> controllers.web.admin.routes.TutorialScriptsCRUD.tutorial(v.toString).absoluteURL(secure = false))
    }
  }

  def tutorial(platform: String) = Authenticated { implicit request =>

    api.getCommonTutorial(GetCommonTutorialRequest(TutorialPlatform.withName(platform))) match {
      case OkApiResult(GetCommonTutorialResult(elements)) =>
        Ok(views.html.admin.tutorialScripts(Menu(request), leftMenu, platform, elements))
      case _ =>
        Ok(views.html.admin.tutorialScripts(Menu(request), leftMenu, platform, List.empty))
    }
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

