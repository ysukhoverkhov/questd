package controllers.domain.admin

import components.DBAccessor
import controllers.domain._
import controllers.domain.helpers._
import models.domain._
import play.Logger

case class AllThemesRequest(sorted: Boolean)

case class AllThemesResult(themes: Iterator[Theme])

case class CreateThemeRequest(theme: Theme)

case class CreateThemeResult()

case class UpdateThemeRequest(theme: Theme)

case class UpdateThemeResult()

case class DeleteThemeRequest(id: String)

case class DeleteThemeResult()

case class GetThemeRequest(id: String)

case class GetThemeResult(theme: Theme)

private[domain] trait ThemesAdminAPI {
  this: DBAccessor =>

  /**
   * List all themes
   */
  def allThemes(request: AllThemesRequest): ApiResult[AllThemesResult] = handleDbException {
    Logger.debug("Admin request for all themes.")

    if (request.sorted)
      OkApiResult(AllThemesResult(db.theme.allWithParams(sorted = true)))
    else
      OkApiResult(AllThemesResult(db.theme.all))
  }

  /**
   * Create new theme
   */
  def createTheme(request: CreateThemeRequest): ApiResult[CreateThemeResult] = handleDbException {
    import models.domain.base.ID

    Logger.debug("Admin request for create new theme.")

    db.theme.create(request.theme.copy(id = ID.generateUUID()))

    OkApiResult(CreateThemeResult())
  }

  /**
   * Update new theme
   */
  def updateTheme(request: UpdateThemeRequest): ApiResult[UpdateThemeResult] = handleDbException {
    Logger.debug("Admin request for update a theme " + request.theme.id)

    // Update allowed here.
    db.theme.update(request.theme)

    OkApiResult(UpdateThemeResult())
  }

  /**
   * Get a theme
   */
  def getTheme(request: GetThemeRequest): ApiResult[GetThemeResult] = handleDbException {
    Logger.debug("Admin request for getting theme by id.")

    db.theme.readById(request.id) ifSome { r =>
      OkApiResult(GetThemeResult(r))
    }
  }

  /**
   * Delete theme.
   */
  def deleteTheme(request: DeleteThemeRequest): ApiResult[DeleteThemeResult] = handleDbException {
    Logger.debug("Admin request for delete theme " + request.id.toString)
    db.theme.delete(request.id)

    OkApiResult(DeleteThemeResult())
  }

}


