package controllers.domain

import models.store._
import play.Logger
import helpers._

import models.domain.theme._


case class AllThemesResult(themes: List[Theme])


private [domain] trait ThemesAdminAPI { this: DomainAPIComponent#DomainAPI => 


  /**
   * Login with FB. Or create new one if it doesn't exists.
   */
  def allThemes: ApiResult[AllThemesResult] = handleDbException {
    Logger.debug("Admin request for all themes.")

    OkApiResult(Some(AllThemesResult(db.theme.allThemes)))
  }


}


