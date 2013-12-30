package controllers.domain.admin

import play.Logger

import components.DBAccessor
import models.store._
import models.domain._
import controllers.domain.helpers.exceptionwrappers._
import controllers.domain._

case class AllThemesResult(themes: List[Theme])

case class CreateThemeRequest(theme: Theme)
case class CreateThemeResult()

case class UpdateThemeRequest(theme: Theme)
case class UpdateThemeResult()

case class DeleteThemeRequest(id: String)
case class DeleteThemeResult()

case class GetThemeRequest(id: String)
case class GetThemeResult(theme: Theme)


private [domain] trait ThemesAdminAPI { this: DBAccessor => 


  /**
   * List all themes
   */
  def allThemes: ApiResult[AllThemesResult] = handleDbException {
    Logger.debug("Admin request for all themes.")

    OkApiResult(Some(AllThemesResult(List() ++ db.theme.all)))
  }

  /**
   * Create new theme
   */
  def createTheme(request: CreateThemeRequest): ApiResult[CreateThemeResult] = handleDbException {
    Logger.debug("Admin request for create new theme.")

    db.theme.create(request.theme)
    
    OkApiResult(Some(CreateThemeResult()))
  }

  /**
   * Update new theme
   */
  def updateTheme(request: UpdateThemeRequest): ApiResult[UpdateThemeResult] = handleDbException {
    Logger.debug("Admin request for update a theme " + request.theme.id)

    // Update allowed here.
    db.theme.update(request.theme)
    
    OkApiResult(Some(UpdateThemeResult()))
  }


  /**
   * Get a theme
   */
  def getTheme(request: GetThemeRequest): ApiResult[GetThemeResult] = handleDbException {
    Logger.debug("Admin request for create new theme.")

    db.theme.readByID(request.id) match {
      case Some(r) => OkApiResult(Some(GetThemeResult(r)))
      case None => NotFoundApiResult()
    }
    
  }

  
  /**
   * Delete theme.
   */
  def deleteTheme(request: DeleteThemeRequest): ApiResult[DeleteThemeResult] = handleDbException {
    Logger.debug("Admin request for delete theme " + request.id.toString)

    db.theme.delete(request.id)
    
    OkApiResult(Some(DeleteThemeResult()))
  }

}


