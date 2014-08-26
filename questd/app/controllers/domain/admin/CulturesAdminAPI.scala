package controllers.domain.admin

import play.Logger

import components.DBAccessor
import models.store._
import models.domain._
import controllers.domain.helpers._
import controllers.domain._

case class AllCulturesRequest()
case class AllCulturesResult(cultures: Iterator[Culture])

case class CreateCultureRequest(culture: Culture)
case class CreateCultureResult()

case class UpdateCultureRequest(culture: Culture)
case class UpdateCultureResult()

case class DeleteCultureRequest(id: String)
case class DeleteCultureResult()

case class GetCultureRequest(id: String)
case class GetCultureResult(culture: Culture)

private[domain] trait CulturesAdminAPI { this: DBAccessor =>

  /**
   * List all themes
   */
  def allCultures(request: AllCulturesRequest): ApiResult[AllCulturesResult] = handleDbException {
    Logger.debug("Admin request for all Cultures.")

      OkApiResult(AllCulturesResult(db.culture.all))
  }

  /**
   * Create new Culture
   */
  def createCulture(request: CreateCultureRequest): ApiResult[CreateCultureResult] = handleDbException {
    import models.domain.base.ID

    Logger.debug("Admin request for create new Culture.")

    db.culture.create(request.culture.copy(id = ID.generateUUID()))

    OkApiResult(CreateCultureResult())
  }

  /**
   * Update Culture
   */
  def updateCulture(request: UpdateCultureRequest): ApiResult[UpdateCultureResult] = handleDbException {
    Logger.debug("Admin request for update a Culture " + request.culture.id)

    db.culture.update(request.culture)

    OkApiResult(UpdateCultureResult())
  }

  /**
   * Get a Culture
   */
  def getCulture(request: GetCultureRequest): ApiResult[GetCultureResult] = handleDbException {
    Logger.debug("Admin request for getting Culture by id.")

    db.culture.readById(request.id) match {
      case Some(r) => OkApiResult(GetCultureResult(r))
      case None => NotFoundApiResult()
    }
  }

  /**
   * Delete Culture.
   */
  def deleteCulture(request: DeleteCultureRequest): ApiResult[DeleteCultureResult] = handleDbException {
    Logger.debug("Admin request for delete Culture " + request.id.toString)
    db.culture.delete(request.id)

    OkApiResult(DeleteCultureResult())
  }

}


