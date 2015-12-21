package controllers.web.admin.component

import controllers.domain.admin._
import controllers.domain.{DomainAPIComponent, OkApiResult}
import models.domain.quest.QuestStatus
import play.api.Play.current
import play.api._
import play.api.data.Forms._
import play.api.data._
import play.api.i18n.Messages.Implicits._
import play.api.mvc._

case class QuestForm(
  id: String,
  status: String,
  level: Int,
  description: String,
  points: Int,
  cheating: Int,
  votersCount: Int)

case class QuestStatusForm(
  questStatus: String)

class QuestsCRUDImpl(val api: DomainAPIComponent#DomainAPI) extends Controller with SecurityAdminImpl {

  private val form = Form(
    mapping(
      "id" -> text,
      "status" -> nonEmptyText,
      "level" -> number,
      "description" -> nonEmptyText,
      "points" -> number,
      "cheating" -> number,
      "votersCount" -> number)(QuestForm.apply)(QuestForm.unapply))

  private def selectedQuestStatus(implicit request: AuthenticatedRequest[AnyContent]): Option[String] = {
    request.cookies.get("questStatus").map(_.value)
  }

  /**
   * Get all quests
   */
  def quests(id: String) = Authenticated { implicit request =>

    // Filling form.
    val f = if (id == "") {
      form
    } else {
      api.getQuestAdmin(GetQuestAdminRequest(id)) match {
        case OkApiResult(GetQuestAdminResult(Some(quest))) =>
          form.fill(QuestForm(
            id = quest.id,
            status = quest.status.toString,
            level = quest.info.level,
            description = quest.info.content.description,
            points = quest.rating.timelinePoints,
            cheating = quest.rating.cheating,
            votersCount = quest.rating.votersCount))
        case _ => form
      }
    }

    // Filling table.
    api.allQuests(AllQuestsRequest()) match {

      case OkApiResult(a: AllQuestsResult) =>
        val statuses = selectedQuestStatus.fold {
          QuestStatus.values.map(_.toString).toList
        }{
          case "All" => QuestStatus.values.map(_.toString).toList
          case v => List(v)
        }

        Ok(views.html.admin.quests(
          Menu(request),
          a.quests.filter(q => statuses.contains(q.status.toString)).toList,
          f))

      case _ => Ok("Internal server error - themes not received.")
    }
  }

  /**
   * Updates quest status by request from CRUD
   */
  def updateQuest() = Authenticated { implicit request =>
    form.bindFromRequest.fold(

      formWithErrors => {

        Logger.error(s"$formWithErrors.errors")

        BadRequest(views.html.admin.quests(
          Menu(request),
          List.empty,
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
            questForm.points,
            questForm.cheating,
            questForm.votersCount))
        }

        Redirect(controllers.web.admin.routes.QuestsCRUD.quests(questForm.id))
      })
  }


  /**
   * QuestStatus filter.
   */
  def selectQuestStatus() = Authenticated { implicit request =>
    val form = Form(
      mapping(
        "questStatus" -> text)(QuestStatusForm.apply)(QuestStatusForm.unapply))

    form.bindFromRequest.fold(
      formWithErrors => {
        Logger.error(s"${formWithErrors.errors}")
        Redirect(controllers.web.admin.routes.QuestsCRUD.quests(""))
      },

      questStatusForm => {
        if (questStatusForm.questStatus == "") {
          Redirect(controllers.web.admin.routes.QuestsCRUD.quests("")).discardingCookies(DiscardingCookie("questStatus"))
        } else {
          Redirect(controllers.web.admin.routes.QuestsCRUD.quests("")).withCookies(Cookie("questStatus", questStatusForm.questStatus))
        }
      })
  }



}


