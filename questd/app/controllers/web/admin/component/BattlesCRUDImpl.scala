package controllers.web.admin.component

import controllers.domain.admin.{AllBattlesRequest, AllBattlesResult}
import controllers.domain.{DomainAPIComponent, OkApiResult}
import play.api.mvc._

class BattlesCRUDImpl(val api: DomainAPIComponent#DomainAPI) extends Controller with SecurityAdminImpl {

  /**
   * Get all solutions
   */
  def battles(id: String) = Authenticated { implicit request =>

    // Filling table.
    api.allBattles(AllBattlesRequest()) match {

      case OkApiResult(a: AllBattlesResult) => Ok(
        views.html.admin.battles(
          Menu(request),
          a.battles.toList/*.sortBy(_.info.questId)*/))

      case _ => Ok("Internal server error - battles not received.")
    }
  }

}

