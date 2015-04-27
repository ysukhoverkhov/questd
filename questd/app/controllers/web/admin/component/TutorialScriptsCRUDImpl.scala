package controllers.web.admin.component

import controllers.domain.app.user.{GetCommonTutorialRequest, GetCommonTutorialResult}
import controllers.domain.{DomainAPIComponent, OkApiResult}
import models.domain._
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._

case class TutorialEntityTypeForm(
  entityType: String)

case class KeyValueForm(
  key: String,
  value: String)

class TutorialScriptsCRUDImpl (val api: DomainAPIComponent#DomainAPI) extends Controller with SecurityAdminImpl {

  private def leftMenu(implicit request: RequestHeader): Map[String, String] = {
    TutorialPlatform.values.foldLeft[Map[String, String]](Map.empty) {
      (c, v) => c + (v.toString -> controllers.web.admin.routes.TutorialScriptsCRUD.tutorial(v.toString).absoluteURL(secure = false))
    }
  }

  private def findTutorialElement(platform: String, elementId: String): Option[TutorialElement] = {
    api.db.tutorial.readById(platform).flatMap(_.elements.find(_.id == elementId))
  }

  private def deleteParamFromActionImpl(platform: String, elementId: String, paramKey: String): Unit = {
    findTutorialElement(platform, elementId) match {
      case Some(e) =>
        val updatedElement = e.copy(action = e.action.copy(params = e.action.params - paramKey))
        api.db.tutorial.updateElement(platform, updatedElement)

      case None =>
        Logger.error(s"Tutorial script or element not found")
    }
  }

  private def addParamToElementActionImpl(platform: String, elementId: String, key: String, value: String): Unit = {
    findTutorialElement(platform, elementId) match {
      case Some(e) =>
        val updatedElement = e.copy(action = e.action.copy(params = e.action.params + (key -> value)))
        api.db.tutorial.updateElement(platform, updatedElement)

      case None =>
        Logger.error(s"Tutorial script or element not found")
    }
  }

  private def addParamToElementConditionImpl(platform: String, elementId: String, conditionIndex:Int, key: String, value: String): Unit = {
    findTutorialElement(platform, elementId) match {
      case Some(e) =>
        val updatedCondition = e.conditions(conditionIndex).copy(params = e.conditions(conditionIndex).params + (key -> value))
        val updatedElement = e.copy(conditions = e.conditions.take(conditionIndex) ++ List(updatedCondition) ++ e.conditions.drop(conditionIndex + 1))
        api.db.tutorial.updateElement(platform, updatedElement)

      case None =>
        Logger.error(s"Tutorial script or element not found")
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
      possibleActions = TutorialActionType.values.map(_.toString).toList,
      possibleConditions = TutorialConditionType.values.map(_.toString).toList))
  }

  def updateAction(platform: String, elementId: String) = Authenticated { implicit request =>

    val form = Form(
    mapping(
      "entityType" -> nonEmptyText)(TutorialEntityTypeForm.apply)(TutorialEntityTypeForm.unapply))

    form.bindFromRequest.fold(
      formWithErrors => {
        Logger.error(s"$formWithErrors.errors")
      },

      actionForm => {
        findTutorialElement(platform, elementId) match {
          case Some(e) =>
            val updatedElement = e.copy(action = e.action.copy(actionType = TutorialActionType.withName(actionForm.entityType)))
            api.db.tutorial.updateElement(platform, updatedElement)

          case None =>
            Logger.error(s"Tutorial script or element not found")
        }
      })

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

  /**
   * Deletes element from tutorial script.
   *
   * @param platform Platform of the script.
   * @param elementId Id if element to delete.
   * @return Content to display.
   */
  def deleteElement(platform: String, elementId: String) = Authenticated { implicit request =>
    api.db.tutorial.deleteElement(platform, elementId)
    Redirect(controllers.web.admin.routes.TutorialScriptsCRUD.tutorial(platform))
  }

  /**
   * Adds empty param to element's action.
   *
   * @param platform Platform of the script.
   * @param elementId Id if element to delete.
   * @return Content to display.
   */
  def addParamToElementAction(platform: String, elementId: String) = Authenticated { implicit request =>

    addParamToElementActionImpl(platform, elementId, "", "")

    Redirect(controllers.web.admin.routes.TutorialScriptsCRUD.tutorial(platform))
  }

  /**
   * Remove parameter from action by its key.
   *
   * @param platform Platform of tutorial element.
   * @param elementId Id of the element.
   * @param paramKey Key of param we should remove.
   * @return Updated content.
   */
  def deleteParamFromElementAction(platform: String, elementId: String, paramKey: String) = Authenticated { implicit request =>
    deleteParamFromActionImpl(platform, elementId, paramKey)
    Redirect(controllers.web.admin.routes.TutorialScriptsCRUD.tutorial(platform))
  }

  /**
   * Saves param in action.
   *
   * @param platform Platform of element with action.
   * @param elementId Id of element to save param in.
   * @param paramKey Key of the parameter to save.
   * @return Content.
   */
  def saveParamInElementAction(platform: String, elementId: String, paramKey: String) = Authenticated { implicit request =>

    val form = Form(
      mapping(
        "key" -> nonEmptyText,
        "value" -> nonEmptyText)(KeyValueForm.apply)(KeyValueForm.unapply))

    form.bindFromRequest.fold(
      formWithErrors => {
        Logger.error(s"${formWithErrors.errors}")
      },

      keyValueForm => {
        deleteParamFromActionImpl(platform, elementId, paramKey)
        addParamToElementActionImpl(platform, elementId, keyValueForm.key, keyValueForm.value)
      })

    Redirect(controllers.web.admin.routes.TutorialScriptsCRUD.tutorial(platform))
  }


  /**
   * Adds new condition to element.
   *
   * @param platform Platform of the element.
   * @param elementId Id of element to add condition to.
   * @return Content.
   */
  def addConditionToElement(platform: String, elementId: String) = Authenticated { implicit request =>

    val tc = TutorialCondition(TutorialConditionType.TutorialElementClosed)

    findTutorialElement(platform, elementId) match {
      case Some(e) =>
        val updatedElement = e.copy(conditions = tc :: e.conditions)
        api.db.tutorial.updateElement(platform, updatedElement)

      case None =>
        Logger.error(s"Tutorial script or element not found")
    }

    Redirect(controllers.web.admin.routes.TutorialScriptsCRUD.tutorial(platform))
  }


  /**
   * Deletes condition from element.
   *
   * @param platform Platform element in.
   * @param elementId Id of element.
   * @param conditionIndex Index of condition to delete.
   * @return Content.
   */
  def deleteConditionFromElement(platform: String, elementId: String, conditionIndex: Int) = Authenticated { implicit request =>

    findTutorialElement(platform, elementId) match {
      case Some(e) =>
        val updatedElement = e.copy(conditions = e.conditions.take(conditionIndex) ++ e.conditions.drop(conditionIndex + 1))
        api.db.tutorial.updateElement(platform, updatedElement)

      case None =>
        Logger.error(s"Tutorial script or element not found")
    }

    Redirect(controllers.web.admin.routes.TutorialScriptsCRUD.tutorial(platform))
  }

  /**
   * Updates type of condition with form.
   *
   * @param platform Platform element in.
   * @param elementId Id of element.
   * @param conditionIndex Index of condition to delete.
   * @return Content.
   */
  def updateCondition(platform: String, elementId: String, conditionIndex: Int) = Authenticated { implicit request =>

    val form = Form(
      mapping(
        "entityType" -> nonEmptyText)(TutorialEntityTypeForm.apply)(TutorialEntityTypeForm.unapply))

    form.bindFromRequest.fold(
      formWithErrors => {
        Logger.error(s"${formWithErrors.errors}")
      },

      entityTypeForm => {
        findTutorialElement(platform, elementId) match {
          case Some(e) =>
            val updatedCondition = e.conditions(conditionIndex).copy(conditionType = TutorialConditionType.withName(entityTypeForm.entityType))
            val updatedElement = e.copy(conditions = e.conditions.take(conditionIndex) ++ List(updatedCondition) ++ e.conditions.drop(conditionIndex + 1))
            api.db.tutorial.updateElement(platform, updatedElement)

          case None =>
            Logger.error(s"Tutorial script or element not found")
        }
      })

    Redirect(controllers.web.admin.routes.TutorialScriptsCRUD.tutorial(platform))
  }

  /**
   * Adds param to specific condition.
   *
   * @param platform Platform element in.
   * @param elementId Id of element.
   * @param conditionIndex Index of condition to delete.
   * @return Content.
   */
  def addParamToElementCondition(platform: String, elementId: String, conditionIndex: Int) = Authenticated { implicit request =>

    addParamToElementConditionImpl(platform, elementId, conditionIndex, "", "")

    Redirect(controllers.web.admin.routes.TutorialScriptsCRUD.tutorial(platform))
  }
}

