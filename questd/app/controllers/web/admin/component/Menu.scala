package controllers.web.admin.component

import play.api._
import play.api.mvc._

private[admin] object Menu {
  def apply(implicit request: RequestHeader) = Map(
      "Home" -> controllers.web.admin.routes.AdminApp.index.absoluteURL(false),
      "Config" -> controllers.web.admin.routes.Config.config("").absoluteURL(false),
      "Themes" -> controllers.web.admin.routes.ThemesCRUD.themes("").absoluteURL(false),
      "Users" -> controllers.web.admin.routes.UsersCRUD.users().absoluteURL(false),
      "Quests" -> controllers.web.admin.routes.QuestsCRUD.quests("").absoluteURL(false),
      "Solutions" -> controllers.web.admin.routes.SolutionsCRUD.solutions().absoluteURL(false),
      "Cultures" -> controllers.web.admin.routes.CulturesCRUD.cultures("").absoluteURL(false),
      "Tutorial Tasks" -> controllers.web.admin.routes.TutorialTasksCRUD.tutorialTasks("").absoluteURL(false)
      )
}
