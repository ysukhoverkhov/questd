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

case class TutorialTaskForm(
  id: String,
  description: String,
  taskType: String,
  requiredCount: Int)

trait TutorialTasksCRUDImpl extends Controller { this: APIAccessor =>

  private val form = Form(
    mapping(
      "id" -> text,
      "description" -> nonEmptyText,
      "taskType" -> nonEmptyText,
      "requiredCount" -> number)(TutorialTaskForm.apply)(TutorialTaskForm.unapply))

  /**
   * Get all tutorial tasks
   */
  def tutorialTasks(id: String) = Action { implicit request =>

    // Filling form.
    val f = if (id == "") {
      form
    } else {
      api.getTutorialTaskAdmin(GetTutorialTaskAdminRequest(id)) match {
        case OkApiResult(GetTutorialTaskAdminResult(Some(task))) => {
          form.fill(TutorialTaskForm(
            id = task.id,
            description = task.description,
            taskType = task.taskType.toString,
            requiredCount = task.requiredCount))
        }
        case _ => form
      }
    }

    // Filling table.
    api.allTutorialTasks(AllTutorialTasksRequest()) match {

      case OkApiResult(a: AllTutorialTasksResult) => Ok(
        views.html.admin.tutorialTasks(
          Menu(request),
          a.tasks.toList,
          f))

      case _ => Ok("Internal server error - themes not received.")
    }
  }

  /**
   * Updates task from CRUD
   */
  def updateTutorialTask = Action { implicit request =>
    form.bindFromRequest.fold(

      formWithErrors => {

        Logger.error(formWithErrors.errors.toString)

        BadRequest(views.html.admin.tutorialTasks(
          Menu(request),
          List(),
          formWithErrors))
      },

      taskForm => {

        if (taskForm.id == "") {
          //          val theme = Theme(id = "", text = themeForm.text, comment = themeForm.comment)
          //          api.createTheme(CreateThemeRequest(theme))
        } else {
          api.updateTutorialTaskAdmin(UpdateTutorialTaskAdminRequest(
              TutorialTask(
                  description = taskForm.description,
                  taskType = TaskType.withName(taskForm.taskType),
                  requiredCount = taskForm.requiredCount)))
        }

        Redirect(controllers.web.admin.routes.TutorialTasksCRUD.entries(taskForm.id))
      })
  }

}


