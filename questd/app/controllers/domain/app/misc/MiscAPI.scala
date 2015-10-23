package controllers.domain.app.misc

import java.util.Date

import com.github.nscala_time.time.Imports._
import components._
import controllers.domain._
import controllers.domain.helpers._
import models.domain.user.User
import org.joda.time.DateTime
import play.Play

case class GetTimeRequest(user: User)
case class GetTimeResult(time: Date)

case class GetCountryListRequest(user: User)
case class GetCountryListResult(countries: List[String])


private[domain] trait MiscAPI { this: DBAccessor =>

  /**
   * Get server's time.
   */
  def getTime(request: GetTimeRequest): ApiResult[GetTimeResult] = handleDbException {
    OkApiResult(GetTimeResult(DateTime.now(DateTimeZone.UTC).toDate))
  }

  /**
   * Get list of possible countries.
   */
  def getCountryList(request: GetCountryListRequest): ApiResult[GetCountryListResult] = handleDbException {
    val countries = scala.io.Source.fromFile(Play.application().getFile("conf/countries.txt")).getLines().toList

    OkApiResult(GetCountryListResult(countries))
  }

}

