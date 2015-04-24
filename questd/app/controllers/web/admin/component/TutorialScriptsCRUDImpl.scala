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

    val els = api.getCommonTutorial(GetCommonTutorialRequest(TutorialPlatform.withName(platform))) match {
      case OkApiResult(GetCommonTutorialResult(elements)) =>
        elements
      case _ =>
        List.empty
    }

    Ok(views.html.admin.tutorialScripts(
      menuItems = Menu(request),
      leftMenuItems = leftMenu,
      currentPlatform = platform,
      elements = els,
      possibleActions = TutorialActionType.values.map(_.toString).toList))
  }

  def updateAction(platform: String, elementId: String) = Authenticated { implicit request =>

    // TODO: update action type here.
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

    Redirect(controllers.web.admin.routes.TutorialScriptsCRUD.tutorial(platform))
  }

  /**
   * Adds new default element to list of elements in tutorial.
   *
   * @param platform platform to add element to.
   * @return Content to display to user.
   */
  def addElement(platform: String) = Authenticated { implicit request =>
    val tc = TutorialCondition(TutorialConditionType.TutorialElementClosed)
    val tt = TutorialTrigger(TutorialTriggerType.Any)
    val te = TutorialElement(
      action = TutorialAction(TutorialActionType.Message),
      conditions = List(tc),
      triggers = List(tt))

    api.db.tutorial.addElement(platform, te)

    Redirect(controllers.web.admin.routes.TutorialScriptsCRUD.tutorial(platform))
  }

  def deleteElement(platform: String, elementId: String) = Authenticated { implicit request =>

    api.db.tutorial.deleteElement(platform, elementId)

    Redirect(controllers.web.admin.routes.TutorialScriptsCRUD.tutorial(platform))
  }

}

