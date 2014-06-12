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

trait QuestsCRUDImpl extends Controller { this: APIAccessor =>

  /**
   * Get all quests
   */
  def quests(id: String) = Action { implicit request =>

    // Filling table.
    api.allQuests(AllQuestsRequest()) match {

      case OkApiResult(Some(a: AllQuestsResult)) => Ok(
        views.html.admin.quests(
          Menu(request),
          a.quests.toList))

      case _ => Ok("Internal server error - themes not received.")
    }
  }

}

