package controllers.web.admin.component

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.ws._
import play.api.libs.json._
import models.domain._
import controllers.domain._
import controllers.domain.admin._
import components._

case class ThemeForm(
  id: String,
  name: String,
  description: String,
  cultureId: String,

  iconType: String,
  iconStorage: String,
  iconReference: String,

  mediaType: String,
  mediaStorage: String,
  mediaReference: String)

class ThemesCRUDImpl(
  val api: DomainAPIComponent#DomainAPI)
  extends Controller
  with BaseCRUDImpl[Theme, ThemeForm] {

  protected final val emptyForm = Form(
    mapping(
      "id" -> text,
      "name" -> nonEmptyText,
      "description" -> nonEmptyText,
      "cultureId" -> nonEmptyText,

      "iconType" -> nonEmptyText,
      "iconStorage" -> nonEmptyText,
      "iconReference" -> nonEmptyText,

      "mediaType" -> nonEmptyText,
      "mediaStorage" -> nonEmptyText,
      "mediaReference" -> nonEmptyText)(ThemeForm.apply)(ThemeForm.unapply))

  /**
   * Form willed with obect.
   */
  protected final def formFilledWithObject(id: String) = {
    api.getTheme(GetThemeRequest(id)) match {
      case OkApiResult(GetThemeResult(theme)) => {

        emptyForm.fill(ThemeForm(
          id = theme.id.toString,
          name = theme.info.name,
          description = theme.info.description,
          cultureId = theme.cultureId,
          iconType = theme.info.icon.get.contentType.toString,
          iconStorage = theme.info.icon.get.storage,
          iconReference = theme.info.icon.get.reference,
          mediaType = theme.info.media.contentType.toString,
          mediaStorage = theme.info.media.storage,
          mediaReference = theme.info.media.reference))
      }
      case _ => emptyForm
    }
  }

  /**
   * All objects crud is for.
   */
  protected final def allObjects = {
    api.allThemes(AllThemesRequest(sorted = false)) match {

      case OkApiResult(a: AllThemesResult) =>
        Some(a.themes.toList)

      case _ => None
    }
  }

  /**
   * Functions what renders HTML
   */
  protected final def renderFunction(request: Request[AnyContent]) = {

    val cultures = api.allCultures(AllCulturesRequest()) match {
      case OkApiResult(a: AllCulturesResult) =>
        a.cultures.toList
      case _ => List()
    }

    views.html.admin.themes(
      Menu(request),
      _: List[Theme],
      cultures, // TODO: pass here map id -> name
      _: Form[ThemeForm])
  }

  /**
   * Delete object.
   */
  protected def deleteObjectWithId(id: String): Unit = {
    api.deleteTheme(DeleteThemeRequest(id))
  }

  /**
   * Home page of our CRUD
   */
  protected val callToHomePage = controllers.web.admin.routes.ThemesCRUD.themes("")

  /**
   * Create culture from its form.
   */
  protected def updateObjectFromForm(form: ThemeForm): Unit = {
    val theme = Theme(
      id = form.id,
      cultureId = form.cultureId,
      info = ThemeInfo(
        name = form.name,
        description = form.description,
        icon = Some(ContentReference(
          contentType = ContentType.withName(form.iconType),
          storage = form.iconStorage,
          reference = form.iconReference)),
        media = ContentReference(
          contentType = ContentType.withName(form.mediaType),
          storage = form.mediaStorage,
          reference = form.mediaReference)))

    if (theme.id == "") {
      api.createTheme(CreateThemeRequest(theme))
    } else {
      api.updateTheme(UpdateThemeRequest(theme))
    }
  }

}

