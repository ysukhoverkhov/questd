package controllers.web.admin.component

import controllers.domain.app.user.{GetCommonTutorialRequest, GetCommonTutorialResult}
import controllers.domain.{DomainAPIComponent, OkApiResult}
import models.domain.tutorial._
import org.json4s.ext.EnumNameSerializer
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._

case class TutorialEntityTypeForm(
  entityType: String)

case class KeyValueForm(
  key: String,
  value: String)

case class SectionNameForm(
  sectionName: String)

class TutorialScriptsCRUDImpl (val api: DomainAPIComponent#DomainAPI) extends Controller with SecurityAdminImpl {

  private def leftMenu(implicit request: RequestHeader): Map[String, String] = {
    TutorialPlatform.values.foldLeft[Map[String, String]](Map.empty) {
      (c, v) => c + (v.toString -> controllers.web.admin.routes.TutorialScriptsCRUD.tutorial(v.toString).absoluteURL(secure = false))
    }
  }

  private def findTutorialElement(platform: String, elementId: String): Option[TutorialElement] = {
    api.db.tutorial.readById(platform).flatMap(_.elements.find(_.id == elementId))
  }

  private def findTutorialElementWithoutSection(platform: String): Option[TutorialElement] = {
    api.db.tutorial.readById(platform).flatMap(_.elements.find(_.crud.sectionName.getOrElse("") == ""))
  }

  private def deleteParamFromActionImpl(platform: String, elementId: String, actionIndex: Int, paramKey: String): Unit = {
    findTutorialElement(platform, elementId) match {
      case Some(e) =>
        val updatedAction = e.actions(actionIndex).copy(params = e.actions(actionIndex).params - paramKey)
        val updatedElement = e.copy(actions = e.actions.take(actionIndex) ++ List(updatedAction) ++ e.actions.drop(actionIndex + 1))
        api.db.tutorial.updateElement(platform, updatedElement)

      case None =>
        Logger.error(s"Tutorial script or element not found")
    }
  }

  private def addParamToElementActionImpl(platform: String, elementId: String, actionIndex: Int, key: String, value: String): Unit = {
    findTutorialElement(platform, elementId) match {
      case Some(e) =>
        val updatedAction = e.actions(actionIndex).copy(params = e.actions(actionIndex).params + (key -> value))
        val updatedElement = e.copy(actions = e.actions.take(actionIndex) ++ List(updatedAction) ++ e.actions.drop(actionIndex + 1))

        api.db.tutorial.updateElement(platform, updatedElement)

      case None =>
        Logger.error(s"Tutorial script or element not found")
    }
  }

  private def addParamToElementConditionImpl(platform: String, elementId: String, conditionIndex: Int, key: String, value: String): Unit = {
    findTutorialElement(platform, elementId) match {
      case Some(e) =>
        val updatedCondition = e.conditions(conditionIndex).copy(params = e.conditions(conditionIndex).params + (key -> value))
        val updatedElement = e.copy(conditions = e.conditions.take(conditionIndex) ++ List(updatedCondition) ++ e.conditions.drop(conditionIndex + 1))
        api.db.tutorial.updateElement(platform, updatedElement)

      case None =>
        Logger.error(s"Tutorial script or element not found")
    }
  }

  private def deleteParamToElementConditionImpl(platform: String, elementId: String, conditionIndex: Int, paramKey: String): Unit = {
    findTutorialElement(platform, elementId) match {
      case Some(e) =>
        val updatedCondition = e.conditions(conditionIndex).copy(params = e.conditions(conditionIndex).params - paramKey)
        val updatedElement = e.copy(conditions = e.conditions.take(conditionIndex) ++ List(updatedCondition) ++ e.conditions.drop(conditionIndex + 1))
        api.db.tutorial.updateElement(platform, updatedElement)

      case None =>
        Logger.error(s"Tutorial script or element not found")
    }
  }

  private def addParamToElementTriggerImpl(platform: String, elementId: String, index: Int, key: String, value: String): Unit = {
    findTutorialElement(platform, elementId) match {
      case Some(e) =>
        val updatedTrigger = e.triggers(index).copy(params = e.triggers(index).params + (key -> value))
        val updatedElement = e.copy(triggers = e.triggers.take(index) ++ List(updatedTrigger) ++ e.triggers.drop(index + 1))
        api.db.tutorial.updateElement(platform, updatedElement)

      case None =>
        Logger.error(s"Tutorial script or element not found")
    }
  }

  private def deleteParamToElementTriggerImpl(platform: String, elementId: String, index: Int, paramKey: String): Unit = {
    findTutorialElement(platform, elementId) match {
      case Some(e) =>
        val updatedTrigger = e.triggers(index).copy(params = e.triggers(index).params - paramKey)
        val updatedElement = e.copy(triggers = e.triggers.take(index) ++ List(updatedTrigger) ++ e.triggers.drop(index + 1))
        api.db.tutorial.updateElement(platform, updatedElement)

      case None =>
        Logger.error(s"Tutorial script or element not found")
    }
  }

  private def selectedSectionName(implicit request: AuthenticatedRequest[AnyContent]): Option[String] = {
    request.cookies.get("sectionName").map(_.value)
  }

  /**
   * Tutorial index page.
   *
   * @param platform Platform to display tutorial for.
   * @return Content of html.
   */
  def tutorial(platform: String) = Authenticated { implicit request =>

    val els = api.getCommonTutorial(GetCommonTutorialRequest(TutorialPlatform.withName(platform))) match {
      case OkApiResult(GetCommonTutorialResult(elements)) =>
        elements
      case _ =>
        List.empty
    }

    val sectionName = selectedSectionName

    val els2 = if (sectionName.isDefined) {
      els.filter {e => sectionName == e.crud.sectionName || ((e.crud.sectionName.getOrElse("") == "") && sectionName.contains("Empty")) }
    } else {
      els
    }

    val allSections = els.foldLeft[Set[String]](Set("")){(r, v) => r + v.crud.sectionName.getOrElse("")}.toList.sorted

    Ok(views.html.admin.tutorialScripts(
      menuItems = Menu(request),
      leftMenuItems = leftMenu,
      currentPlatform = platform,
      elements = els2,
      allSections = allSections))
  }

  /**
   * Updates action.
   *
   * @param platform Platform to update action fro.
   * @param elementId Id of element with action.
   * @param actionIndex Acion index.
   * @return redirect.
   */
  def updateAction(platform: String, elementId: String, actionIndex: Int) = Authenticated { implicit request =>

    val form = Form(
    mapping(
      "entityType" -> nonEmptyText)(TutorialEntityTypeForm.apply)(TutorialEntityTypeForm.unapply))

    form.bindFromRequest.fold(
      formWithErrors => {
        Logger.error(s"$formWithErrors.errors")
      },

      actionTypeForm => {
        findTutorialElement(platform, elementId) match {
          case Some(e) =>
            val updatedAction = e.actions(actionIndex).copy(actionType = TutorialActionType.withName(actionTypeForm.entityType))
            val updatedElement = e.copy(actions = e.actions.take(actionIndex) ++ List(updatedAction) ++ e.actions.drop(actionIndex + 1))

            api.db.tutorial.updateElement(platform, updatedElement)

          case None =>
            Logger.error(s"Tutorial script or element not found")
        }
      })

    redirectToElement(platform, elementId)
  }


  /**
   * Adds new action to element.
   *
   * @param platform Platform of element.
   * @param elementId Id of element.
   * @return Redirect.
   */
  def addActionToElement(platform: String, elementId: String) = Authenticated { implicit request =>
    val ta = TutorialAction(TutorialActionType.Message)

    findTutorialElement(platform, elementId) match {
      case Some(e) =>
        val updatedElement = e.copy(actions = e.actions ::: List(ta))
        api.db.tutorial.updateElement(platform, updatedElement)

      case None =>
        Logger.error(s"Tutorial script or element not found")
    }

    redirectToElement(platform, elementId)
  }

  /**
   * Deletes action from element.
   *
   * @param platform Platform of element.
   * @param elementId Id of element.
   * @param actionIndex Index of action to delete.
   * @return
   */
  def deleteActionFromElement(platform: String, elementId: String, actionIndex: Int) = Authenticated { implicit request =>
    findTutorialElement(platform, elementId) match {
      case Some(e) =>
        val updatedElement = e.copy(actions = e.actions.take(actionIndex) ++ e.actions.drop(actionIndex + 1))
        api.db.tutorial.updateElement(platform, updatedElement)

      case None =>
        Logger.error(s"Tutorial script or element not found")
    }

    redirectToElement(platform, elementId)
  }


  /**
   * Adds new default element to list of elements in tutorial.
   *
   * @param platform platform to add element to.
   * @return Content to display to user.
   */
  def addElement(platform: String, elementId: Option[String]) = Authenticated { implicit request =>

    ensurePlatformExists(platform)

    val tc = TutorialCondition(TutorialConditionType.TutorialElementClosed)
    val tt = TutorialTrigger(TutorialTriggerType.Any)
    val te = TutorialElement(
      actions = List(TutorialAction(TutorialActionType.Message), TutorialAction(TutorialActionType.CloseTutorialElement)),
      conditions = List(tc),
      triggers = List(tt),
      crud = TutorialElementCRUD(sectionName = selectedSectionName))

    elementId.fold {
      api.db.tutorial.addElement(platform, te)
      redirectToElement(platform, te)
    } { elementId =>
      api.db.tutorial.readById(platform).fold {
        redirectToElement(platform, elementId)
      } { tutorial =>
        tutorial.elements.find(_.id == elementId).fold {
          redirectToElement(platform, elementId)
        } { element =>

          val elementIndex = tutorial.elements.indexOf(element)

          api.db.tutorial.update(tutorial.copy(
            elements = tutorial.elements.take(elementIndex + 1) ++ List(te) ++ tutorial.elements.drop(elementIndex + 1)))
          redirectToElement(platform, elementId)
        }
      }
    }
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
            redirectToElement(platform, elementId)
  }

  /**
   * Moves an element up in list of elements.
   *
   * @param platform Platform element for.
   * @param elementId Id of element to move.
   * @return Content.
   */
  def upElement(platform: String, elementId: String) = Authenticated { implicit request =>

    def swapWithPrev[T](l: List[T], e : T) : List[T] = l match {
      case Nil => Nil
      case prev::`e`::tl => e::prev::tl
      case hd::tl => hd::swapWithPrev(tl, e)
    }

    api.db.tutorial.readById(platform).fold {
      redirectToElement(platform, elementId)
    } { tutorial =>
      tutorial.elements.find(_.id == elementId).fold {
        redirectToElement(platform, elementId)
      } { element =>
        api.db.tutorial.update(tutorial.copy(elements = swapWithPrev(tutorial.elements, element)))
        redirectToElement(platform, elementId)
      }
    }
  }

  /**
   * Moves an element down in list of elements.
   *
   * @param platform Platform element for.
   * @param elementId Id of element to move.
   * @return Content.
   */
  def downElement(platform: String, elementId: String) = Authenticated { implicit request =>

    def swapWithNext[T](l: List[T], e : T) : List[T] = l match {
      case Nil => Nil
      case `e`::next::tl => next::e::tl
      case hd::tl => hd::swapWithNext(tl, e)
    }

    api.db.tutorial.readById(platform).fold {
      redirectToElement(platform, elementId)
    } { tutorial =>
      tutorial.elements.find(_.id == elementId).fold {
        redirectToElement(platform, elementId)
      } { element =>
        api.db.tutorial.update(tutorial.copy(elements = swapWithNext(tutorial.elements, element)))
        redirectToElement(platform, elementId)
      }
    }
  }

  /**
   * Adds empty param to element's action.
   *
   * @param platform Platform of the script.
   * @param elementId Id if element to delete.
   * @return Content to display.
   */
  def addParamToElementAction(platform: String, elementId: String, actionIndex: Int) = Authenticated { implicit request =>
    addParamToElementActionImpl(platform, elementId, actionIndex, "", "")
    redirectToElement(platform, elementId)
  }

  /**
   * Remove parameter from action by its key.
   *
   * @param platform Platform of tutorial element.
   * @param elementId Id of the element.
   * @param paramKey Key of param we should remove.
   * @return Updated content.
   */
  def deleteParamFromElementAction(platform: String, elementId: String, actionIndex: Int, paramKey: String) = Authenticated { implicit request =>
    deleteParamFromActionImpl(platform, elementId, actionIndex, paramKey)
    redirectToElement(platform, elementId)
  }

  /**
   * Saves param in action.
   *
   * @param platform Platform of element with action.
   * @param elementId Id of element to save param in.
   * @param paramKey Key of the parameter to save.
   * @return Content.
   */
  def saveParamInElementAction(platform: String, elementId: String, actionIndex: Int, paramKey: String) = Authenticated { implicit request =>

    val form = Form(
      mapping(
        "key" -> nonEmptyText,
        "value" -> nonEmptyText)(KeyValueForm.apply)(KeyValueForm.unapply))

    form.bindFromRequest.fold(
      formWithErrors => {
        Logger.error(s"${formWithErrors.errors}")
      },

      keyValueForm => {
        deleteParamFromActionImpl(platform, elementId, actionIndex, paramKey)
        addParamToElementActionImpl(platform, elementId, actionIndex, keyValueForm.key, keyValueForm.value)
      })

    redirectToElement(platform, elementId)
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

    redirectToElement(platform, elementId)
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

    redirectToElement(platform, elementId)
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

      redirectToElement(platform, elementId)
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
            redirectToElement(platform, elementId)
  }

  /**
   * Deletes param from condition.
   *
   * @param platform Platform element in.
   * @param elementId Id of element.
   * @param conditionIndex Index of condition to delete.
   * @param paramKey key of param to delete.
   * @return Content.
   */
  def deleteParamFromElemCondition(platform: String, elementId: String, conditionIndex: Int, paramKey: String) = Authenticated { implicit request =>
    deleteParamToElementConditionImpl(platform, elementId, conditionIndex, paramKey)
    redirectToElement(platform, elementId)
  }

  /**
   * Updates param from condition.
   *
   * @param platform Platform element in.
   * @param elementId Id of element.
   * @param conditionIndex Index of condition to update.
   * @param paramKey key of param to update.
   * @return Content.
   */
  def saveParamInElementCondition(platform: String, elementId: String, conditionIndex: Int, paramKey: String) = Authenticated { implicit request =>

    val form = Form(
      mapping(
        "key" -> nonEmptyText,
        "value" -> nonEmptyText)(KeyValueForm.apply)(KeyValueForm.unapply))

    form.bindFromRequest.fold(
      formWithErrors => {
        Logger.error(s"${formWithErrors.errors}")
      },

      keyValueForm => {
        deleteParamToElementConditionImpl(platform, elementId, conditionIndex, paramKey)
        addParamToElementConditionImpl(platform, elementId, conditionIndex, keyValueForm.key, keyValueForm.value)
      })

            redirectToElement(platform, elementId)
  }


  /**
   * Updates trigger's type.
   *
   * @param platform Platform element in.
   * @param elementId Id of element.
   * @param index Index of trigger
   * @return Content.
   */
  def updateTrigger(platform: String, elementId: String, index: Int) = Authenticated { implicit request =>

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
            val updatedTrigger = e.triggers(index).copy(triggerType = TutorialTriggerType.withName(entityTypeForm.entityType))
            val updatedElement = e.copy(triggers = e.triggers.take(index) ++ List(updatedTrigger) ++ e.triggers.drop(index + 1))
            api.db.tutorial.updateElement(platform, updatedElement)

          case None =>
            Logger.error(s"Tutorial script or element not found")
        }
      })

            redirectToElement(platform, elementId)
  }

  /**
   * Adds new trigger to element.
   *
   * @param platform Platform element in.
   * @param elementId Id of element.
   * @return Content.
   */
  def addTriggerToElement(platform: String, elementId: String) = Authenticated { implicit request =>

    val tc = TutorialTrigger(TutorialTriggerType.Any)

    findTutorialElement(platform, elementId) match {
      case Some(e) =>
        val updatedElement = e.copy(triggers = tc :: e.triggers)
        api.db.tutorial.updateElement(platform, updatedElement)

      case None =>
        Logger.error(s"Tutorial script or element not found")
    }

            redirectToElement(platform, elementId)
  }


  /**
   * Deletes specified trigger in element.
   *
   * @param platform Platform element in.
   * @param elementId Id of element.
   * @param index Index of trigger
   * @return Content.
   */
  def deleteTriggerFromElement(platform: String, elementId: String, index: Int) = Authenticated { implicit request =>

    findTutorialElement(platform, elementId) match {
      case Some(e) =>
        if (e.triggers.nonEmpty) {
          val updatedElement = e.copy(triggers = e.triggers.take(index) ++ e.triggers.drop(index + 1))
          api.db.tutorial.updateElement(platform, updatedElement)
        }

      case None =>
        Logger.error(s"Tutorial script or element not found")
    }

    redirectToElement(platform, elementId)
  }

  /**
   * Adds param to trigger in element.
   *
   * @param platform Platform element in.
   * @param elementId Id of element.
   * @param index Index of trigger
   * @return Content.
   */
  def addParamToElementTrigger(platform: String, elementId: String, index: Int) = Authenticated { implicit request =>
    addParamToElementTriggerImpl(platform, elementId, index, "", "")
            redirectToElement(platform, elementId)
  }

  /**
   * Deletes param in trigger.
   *
   * @param platform Platform element in.
   * @param elementId Id of element.
   * @param index Index of trigger
   * @param paramKey Key of param to delete.
   * @return Content.
   */
  def deleteParamFromElemTrigger(platform: String, elementId: String, index: Int, paramKey: String) = Authenticated { implicit request =>
    deleteParamToElementTriggerImpl(platform, elementId, index, paramKey)
            redirectToElement(platform, elementId)
  }

  /**
   * Saves new param value.
   *
   * @param platform Platform element in.
   * @param elementId Id of element.
   * @param index Index of trigger
   * @param paramKey Key of param to delete.
   * @return Content.
   */
  def saveParamInElementTrigger(platform: String, elementId: String, index: Int, paramKey: String) = Authenticated { implicit request =>

    val form = Form(
      mapping(
        "key" -> nonEmptyText,
        "value" -> nonEmptyText)(KeyValueForm.apply)(KeyValueForm.unapply))

    form.bindFromRequest.fold(
      formWithErrors => {
        Logger.error(s"${formWithErrors.errors}")
      },

      keyValueForm => {
        deleteParamToElementTriggerImpl(platform, elementId, index, paramKey)
        addParamToElementTriggerImpl(platform, elementId, index, keyValueForm.key, keyValueForm.value)
      })

      redirectToElement(platform, elementId)
  }

  /**
   * Exports the whole tutorial script for given platform.
   *
   * @param platform Platform to export script for.
   * @return The script.
   */
  def exportTutorialScript(platform: String) = Authenticated { implicit request =>

    import controllers.web.helpers._

    api.db.tutorial.readById(platform) match {
      case Some(t) =>

        Ok(Json.write(t)).withHeaders(CACHE_CONTROL -> "max-age=0", CONTENT_DISPOSITION -> s"attachment; filename=$platform.js", CONTENT_TYPE -> "application/x-download")
      case None =>
        InternalServerError
    }
  }

  /**
   * Imports tutorial script from file.
   *
   * @param platform Platfor to import script for.
   * @return Redirects somewhere.
   */
  def importTutorialScript(platform: String) = Authenticated(parse.multipartFormData) { request =>
    import controllers.web.helpers._

    Logger.debug(s"Importing tutorial script")

    ensurePlatformExists(platform)

    request.body.file("tutorialScript").map { tutorialScript =>

      val serializers = List(
        new EnumNameSerializer(TutorialActionType),
        new EnumNameSerializer(TutorialTriggerType),
        new EnumNameSerializer(TutorialConditionType)
      )

      val tutorial = Json.read[Tutorial](scala.io.Source.fromFile(tutorialScript.ref.file).mkString, serializers)
      api.db.tutorial.update(tutorial.copy(id = platform))

      Redirect(controllers.web.admin.routes.TutorialScriptsCRUD.tutorial(platform))
    }.getOrElse {
      Redirect(controllers.web.admin.routes.TutorialScriptsCRUD.tutorial(platform)).flashing(
        "error" -> "Missing file")
    }
  }

  /**
   * Adds section to first encountered nameless tutorial element.
   *
   * @param platform Platform to search element in.
   * @return Redirect.
   */
  def addSection(platform: String) = Authenticated { implicit request =>
    ensurePlatformExists(platform)

    val form = Form(
      mapping(
        "sectionName" -> text)(SectionNameForm.apply)(SectionNameForm.unapply))

    form.bindFromRequest.fold(
      formWithErrors => {
        Logger.error(s"${formWithErrors.errors}")
      },

      sectionNameForm => {
        findTutorialElementWithoutSection(platform).fold[Any](None) { element =>
          api.db.tutorial.updateElement(platform, element.copy(crud = element.crud.copy(sectionName = if (sectionNameForm.sectionName == "") None else Some(sectionNameForm.sectionName))))
        }
      })

    Redirect(controllers.web.admin.routes.TutorialScriptsCRUD.tutorial(platform))
  }

  /**
   * Sets section name for element.
   *
   * @param platform Platform to search element in.
   * @param elementId id of element to sen section name for.
   * @return Redirect.
   */
  def updateElementSectionName(platform: String, elementId: String) = Authenticated { implicit request =>
    ensurePlatformExists(platform)

    val form = Form(
      mapping(
        "sectionName" -> text)(SectionNameForm.apply)(SectionNameForm.unapply))

    form.bindFromRequest.fold(
      formWithErrors => {
        Logger.error(s"${formWithErrors.errors}")
      },

      sectionNameForm => {
        findTutorialElement(platform, elementId).fold[Any](None) { element =>
          api.db.tutorial.updateElement(platform, element.copy(crud = element.crud.copy(sectionName = Some(sectionNameForm.sectionName))))
        }
      })

    redirectToElement(platform, elementId)
  }

  /**
   * select section to filter.
   *
   * @param platform Platform to search element in.
   * @return
   */
  def selectElementSectionName(platform: String) = Authenticated { implicit request =>
    ensurePlatformExists(platform)

    val form = Form(
      mapping(
        "sectionName" -> text)(SectionNameForm.apply)(SectionNameForm.unapply))

    form.bindFromRequest.fold(
      formWithErrors => {
        Logger.error(s"${formWithErrors.errors}")
        Redirect(controllers.web.admin.routes.TutorialScriptsCRUD.tutorial(platform))
      },

      sectionNameForm => {
        if (sectionNameForm.sectionName == "") {
          Redirect(controllers.web.admin.routes.TutorialScriptsCRUD.tutorial(platform)).discardingCookies(DiscardingCookie("sectionName"))
        } else {
          Redirect(controllers.web.admin.routes.TutorialScriptsCRUD.tutorial(platform)).withCookies(Cookie("sectionName", sectionNameForm.sectionName))
        }
      })
  }


  private def ensurePlatformExists(platform: String): Unit = {
    if (api.db.tutorial.readById(platform).isEmpty) {
      api.db.tutorial.create(Tutorial(id = platform))
    }
  }

  /**
   * Generates result with redirect to specified element and platform.
   *
   * @param platform Platform to redirect to.
   * @param element Element to anchor to.
   * @return Generated result.
   */
  private def redirectToElement(platform: String, element: TutorialElement): Result = {
    redirectToElement(platform, element.id)
  }

  private def redirectToElement(platform: String, elementId: String): Result = {
    val callToTutorial = controllers.web.admin.routes.TutorialScriptsCRUD.tutorial(platform)
    Redirect(callToTutorial.copy(url = callToTutorial.url + s"#$elementId"))
  }
}

