package controllers.domain

import models.store._
import play.Logger
import helpers._

import models.domain.theme._


case class AllThemesResult(themes: List[Theme])

case class CreateThemeRequest(theme: Theme)
case class CreateThemeResult()

private [domain] trait ThemesAdminAPI { this: DomainAPIComponent#DomainAPI => 


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
  def createTheme(theme: Theme): ApiResult[CreateThemeResult] = handleDbException {
    Logger.debug("Admin request for create new theme.")

    val newID = java.util.UUID.randomUUID().toString()
    
    db.theme.createTheme(theme.replaceID(newID))
    
    OkApiResult(Some(CreateThemeResult()))
  }

}


