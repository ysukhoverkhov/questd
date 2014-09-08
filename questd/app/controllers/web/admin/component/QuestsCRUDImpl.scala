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

case class QuestForm(
  id: String,
  status: String,
  level: Int,
  description: String,
  difficulty: String,
  duration: String,
  points: Int,
  cheating: Int,
  votersCount: Int)

trait QuestsCRUDImpl extends Controller with SecurityAdminImpl { this: APIAccessor =>

  private val form = Form(
    mapping(
      "id" -> text,
      "status" -> nonEmptyText,
      "level" -> number,
      "description" -> nonEmptyText,
      "difficulty" -> nonEmptyText,
      "duration" -> nonEmptyText,
      "points" -> number,
      "cheating" -> number,
      "votersCount" -> number)(QuestForm.apply)(QuestForm.unapply))

  /**
   * Get all quests
   */
  def quests(id: String) = Authenticated { implicit request =>

    // Filling form.
    val f = if (id == "") {
      form
    } else {
      api.getQuestAdmin(GetQuestAdminRequest(id)) match {
        case OkApiResult(GetQuestAdminResult(Some(quest))) => {
          form.fill(QuestForm(
            id = quest.id,
            status = quest.status.toString,
            level = quest.info.level,
            description = quest.info.content.description,
            difficulty = quest.info.difficulty.toString,
            duration = quest.info.duration.toString,
            points = quest.rating.points,
            cheating = quest.rating.cheating,
            votersCount = quest.rating.votersCount))
        }
        case _ => form
      }
    }

    // Filling table.
    api.allQuests(AllQuestsRequest()) match {

      case OkApiResult(a: AllQuestsResult) => Ok(
        views.html.admin.quests(
          Menu(request),
          a.quests.toList,
          f))

      case _ => Ok("Internal server error - themes not received.")
    }
  }

  /**
   * Updates quest status by request from CRUD
   */
  def updateQuest = Authenticated { implicit request =>
    form.bindFromRequest.fold(

      formWithErrors => {

        Logger.error(formWithErrors.errors.toString)

        BadRequest(views.html.admin.quests(
          Menu(request),
          List(),
          formWithErrors))
      },

      questForm => {

        if (questForm.id == "") {
          //          val theme = Theme(id = "", text = themeForm.text, comment = themeForm.comment)
          //          api.createTheme(CreateThemeRequest(theme))
        } else {
          api.updateQuestAdmin(UpdateQuestAdminRequest(
            questForm.id,
            questForm.status,
            questForm.level,
            questForm.description,
            questForm.difficulty,
            questForm.duration,
            questForm.points,
            questForm.cheating,
            questForm.votersCount))
        }

        Redirect(controllers.web.admin.routes.QuestsCRUD.quests(questForm.id))
      })
  }

}


