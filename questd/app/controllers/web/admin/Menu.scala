package controllers.web.admin

import play.api._
import play.api.mvc._

private[admin] object Menu {
  def apply(implicit request: RequestHeader) = Map(
      "Home" -> controllers.web.admin.routes.AdminApp.index.absoluteURL(false),
      "Themes" -> controllers.web.admin.routes.AdminApp.themes.absoluteURL(false))
}
