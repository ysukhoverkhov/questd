package controllers.domain.app.theme

import components.DBAccessor
import controllers.domain._
import controllers.domain.helpers._
import models.domain._


case class GetAllThemesForCultureRequest(cultureId: String)

case class GetAllThemesForCultureResult(themes: Iterator[Theme])


private[domain] trait ThemeFetchAPI {
  this: DBAccessor =>

  def getAllThemesForCulture(request: GetAllThemesForCultureRequest): ApiResult[GetAllThemesForCultureResult] = handleDbException {

    OkApiResult(GetAllThemesForCultureResult(db.theme.allWithParams(
      cultureId = Some(request.cultureId),
      sorted = true)))
  }
}
