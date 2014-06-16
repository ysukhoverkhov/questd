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
  difficulty: String,
  duration: String)

trait QuestsCRUDImpl extends Controller { this: APIAccessor =>

  val form = Form(
    mapping(
      "id" -> text,
      "status" -> nonEmptyText,
      "level" -> number,
      "difficulty" -> nonEmptyText,
      "duration" -> nonEmptyText)(QuestForm.apply)(QuestForm.unapply))

  /**
   * Get all quests
   */
  def quests(id: String) = Action { implicit request =>

    // Filling form.
    val f = if (id == "") {
      form
    } else {
      api.getQuestAdmin(GetQuestAdminRequest(id)) match {
        case OkApiResult(Some(GetQuestAdminResult(Some(quest)))) => {
          form.fill(QuestForm(
            id = quest.id,
            status = quest.status.toString,
            level = quest.info.level,
            difficulty = quest.info.difficulty.toString,
            duration = quest.info.duration.toString))
        }
        case _ => form
      }
    }

    // Filling table.
    api.allQuests(AllQuestsRequest()) match {

      case OkApiResult(Some(a: AllQuestsResult)) => Ok(
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
  def updateQuest = Action { implicit request =>
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
              questForm.difficulty,
              questForm.duration))
        }

        Redirect(controllers.web.admin.routes.QuestsCRUD.quests(questForm.id))
      })
  }

}


