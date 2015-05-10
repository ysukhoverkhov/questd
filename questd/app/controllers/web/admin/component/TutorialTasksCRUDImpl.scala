package controllers.web.admin.component

import controllers.domain.admin._
import controllers.domain.{DomainAPIComponent, OkApiResult}
import models.domain._
import play.api._
import play.api.data.Forms._
import play.api.data._
import play.api.mvc._

case class TutorialTaskForm(
  id: String,
  description: String,
  taskType: String,
  requiredCount: Int,
  rewardCoins: Int,
  rewardMoney: Int,
  rewardRating: Int)

class TutorialTasksCRUDImpl(val api: DomainAPIComponent#DomainAPI) extends Controller with SecurityAdminImpl {

  private val form = Form(
    mapping(
      "id" -> text,
      "description" -> nonEmptyText,
      "taskType" -> nonEmptyText,
      "requiredCount" -> number,
      "rewardCoins" -> number,
      "rewardMoney" -> number,
      "rewardRating" -> number)(TutorialTaskForm.apply)(TutorialTaskForm.unapply))

  /**
   * Get all tutorial tasks
   */
  def tutorialTasks(id: String) = Authenticated { implicit request =>

    // Filling form.
    val f = if (id == "") {
      form
    } else {
      api.getTutorialTaskAdmin(GetTutorialTaskAdminRequest(id)) match {
        case OkApiResult(GetTutorialTaskAdminResult(Some(task))) =>
          form.fill(TutorialTaskForm(
            id = task.id,
            description = task.description,
            taskType = task.taskType.toString,
            requiredCount = task.requiredCount,
            rewardCoins = task.reward.coins.toInt,
            rewardMoney = task.reward.money.toInt,
            rewardRating = task.reward.rating.toInt))
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
  def updateTutorialTask() = Authenticated { implicit request =>
    form.bindFromRequest.fold(

      formWithErrors => {

        Logger.error(s"$formWithErrors.errors")

        BadRequest(views.html.admin.tutorialTasks(
          Menu(request),
          List.empty,
          formWithErrors))
      },

      taskForm => {

        val tt = TutorialTask(
          id = taskForm.id,
          description = taskForm.description,
          taskType = TaskType.withName(taskForm.taskType),
          requiredCount = taskForm.requiredCount,
          reward = Assets(
            coins = taskForm.rewardCoins,
            money = taskForm.rewardMoney,
            rating = taskForm.rewardRating))

        if (taskForm.id == "") {
          api.createTutorialTaskAdmin(CreateTutorialTaskAdminRequest(tt))
        } else {
          api.updateTutorialTaskAdmin(UpdateTutorialTaskAdminRequest(tt))
        }

        Redirect(controllers.web.admin.routes.TutorialTasksCRUD.tutorialTasks(""))
      })
  }


  /**
   * Exports tutorial tasks to file.
   *
   * @return Redirect
   */
  def exportTutorialTasks = Authenticated { implicit request =>
    import controllers.web.helpers._

    val tasks = api.db.tutorialTask.all.toList
    Ok(Json.write(tasks)).withHeaders(CACHE_CONTROL -> "max-age=0", CONTENT_DISPOSITION -> s"attachment; filename=tutorialTasks.js", CONTENT_TYPE -> "application/x-download")
  }

  /**
   * Imports Tutorial tasks from file.
   *
   * @return Redirect
   */
  def importTutorialTasks = Authenticated { implicit request =>
    Redirect(controllers.web.admin.routes.TutorialTasksCRUD.tutorialTasks(""))
  }

}


