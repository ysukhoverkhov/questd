package controllers.web.admin.component

import play.api.mvc._

private[admin] object Menu {
  def apply(implicit request: RequestHeader) = Map(
    "Home" -> controllers.web.admin.routes.AdminApp.index().absoluteURL(secure = false),
    "Config" -> controllers.web.admin.routes.Config.config("").absoluteURL(secure = false),
    "Themes" -> controllers.web.admin.routes.ThemesCRUD.themes("").absoluteURL(secure = false),
    "Users" -> controllers.web.admin.routes.UsersCRUD.users().absoluteURL(secure = false),
    "Quests" -> controllers.web.admin.routes.QuestsCRUD.quests("").absoluteURL(secure = false),
    "Solutions" -> controllers.web.admin.routes.SolutionsCRUD.solutions().absoluteURL(secure = false),
    "Battles" -> controllers.web.admin.routes.BattlesCRUD.battles().absoluteURL(secure = false),
    "Cultures" -> controllers.web.admin.routes.CulturesCRUD.cultures("").absoluteURL(secure = false),
    "Tutorial Tasks" -> controllers.web.admin.routes.TutorialTasksCRUD.tutorialTasks("").absoluteURL(secure = false)
  )
}
