package controllers.domain.admin

import models.domain.tutorialtask.TutorialTask
import play.Logger

import components.DBAccessor
import models.store._
import models.domain._
import controllers.domain.helpers._
import controllers.domain._

case class AllTutorialTasksRequest()
case class AllTutorialTasksResult(tasks: Iterator[TutorialTask])

case class GetTutorialTaskAdminRequest(id: String)
case class GetTutorialTaskAdminResult(task: Option[TutorialTask])

case class CreateTutorialTaskAdminRequest(task: TutorialTask)
case class CreateTutorialTaskAdminResult()

case class UpdateTutorialTaskAdminRequest(task: TutorialTask)
case class UpdateTutorialTaskAdminResult()

private[domain] trait TutorialTasksAdminAPI { this: DBAccessor =>

  /**
   * List all tasks
   */
  def allTutorialTasks(request: AllTutorialTasksRequest): ApiResult[AllTutorialTasksResult] = handleDbException {
    Logger.debug("Admin request for all Tutorial Tasks.")

    OkApiResult(AllTutorialTasksResult(db.tutorialTask.all))
  }

  /**
   * Get task by its id.
   */
  def getTutorialTaskAdmin(request: GetTutorialTaskAdminRequest): ApiResult[GetTutorialTaskAdminResult] = handleDbException {
    Logger.debug("Admin request for geting a tutorial task.")

    OkApiResult(GetTutorialTaskAdminResult(db.tutorialTask.readById(request.id)))
  }

  /**
   * Create tutorial task.
   */
  def createTutorialTaskAdmin(request: CreateTutorialTaskAdminRequest): ApiResult[CreateTutorialTaskAdminResult] = handleDbException {
    import models.domain.base.ID

    Logger.debug("Admin request for create tutoral task.")

    db.tutorialTask.create(request.task.copy(id = ID.generate))

    OkApiResult(CreateTutorialTaskAdminResult())
  }

  /**
   * Update tutorial task.
   */
  def updateTutorialTaskAdmin(request: UpdateTutorialTaskAdminRequest): ApiResult[UpdateTutorialTaskAdminResult] = handleDbException {
    Logger.debug("Admin request for update a tutorial task" + request.task.id)

    db.tutorialTask.update(request.task)

    OkApiResult(UpdateTutorialTaskAdminResult())
  }

}

