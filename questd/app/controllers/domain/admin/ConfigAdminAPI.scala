package controllers.domain.admin

import play.Logger

import models.store._
import models.domain.theme._

import controllers.domain._

import components._


//case class AllThemesResult(themes: List[Theme])
//
//case class CreateThemeRequest(theme: Theme)
//case class CreateThemeResult()
//
//case class UpdateThemeRequest(theme: Theme)
//case class UpdateThemeResult()
//
//case class DeleteThemeRequest(id: ThemeID)
//case class DeleteThemeResult()
//
//case class GetThemeRequest(id: ThemeID)
//case class GetThemeResult(theme: Theme)


private [domain] trait ConfigAdminAPI { this: DBAccessor => 
/*

  /**
   * List all themes
   */
  def allThemes: ApiResult[AllThemesResult] = handleDbException {
    Logger.debug("Admin request for all themes.")

    OkApiResult(Some(AllThemesResult(db.theme.allThemes)))
  }

  /**
   * Create new theme
   */
  def createTheme(request: CreateThemeRequest): ApiResult[CreateThemeResult] = handleDbException {
    Logger.debug("Admin request for create new theme.")

    val newID = java.util.UUID.randomUUID().toString()
    
    db.theme.createTheme(request.theme.replaceID(newID))
    
    OkApiResult(Some(CreateThemeResult()))
  }

  /**
   * Update new theme
   */
  def updateTheme(request: UpdateThemeRequest): ApiResult[UpdateThemeResult] = handleDbException {
    Logger.debug("Admin request for update a theme " + request.theme.id)

    db.theme.updateTheme(request.theme)
    
    OkApiResult(Some(UpdateThemeResult()))
  }


  /**
   * Get a theme
   */
  def getTheme(request: GetThemeRequest): ApiResult[GetThemeResult] = handleDbException {
    Logger.debug("Admin request for create new theme.")

    db.theme.readThemeByID(Theme(request.id)) match {
      case Some(r) => OkApiResult(Some(GetThemeResult(r)))
      case None => NotFoundApiResult(None)
    }
    
  }

  
  /**
   * Delete theme.
   */
  def deleteTheme(request: DeleteThemeRequest): ApiResult[DeleteThemeResult] = handleDbException {
    Logger.debug("Admin request for delete theme " + request.id.toString)

    db.theme.deleteTheme(Theme(request.id))
    
    OkApiResult(Some(DeleteThemeResult()))
  }
*/
}


