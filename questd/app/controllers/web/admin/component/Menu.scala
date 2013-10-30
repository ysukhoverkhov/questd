package controllers.web.admin.component

import play.api._
import play.api.mvc._

private[admin] object Menu {
  def apply(implicit request: RequestHeader) = Map(
      "Home" -> controllers.web.admin.routes.AdminApp.index.absoluteURL(false),
      "Themes" -> controllers.web.admin.routes.ThemesCRUD.themes.absoluteURL(false))
}
