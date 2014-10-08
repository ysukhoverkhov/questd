package controllers.domain.app.user

import controllers.domain.app.protocol.ProfileModificationResult._
import models.domain._
import controllers.domain.DomainAPIComponent
import components._
import controllers.domain._
import controllers.domain.helpers._
import logic._
import play.Logger

case class GetAllUsersRequest()
case class GetAllUsersResult(users: Iterator[User])

case class ResetPurchasesRequest(user: User)
case class ResetPurchasesResult()

case class AdjustAssetsRequest(user: User, reward: Option[Assets] = None, cost: Option[Assets] = None)
case class AdjustAssetsResult(user: User)

case class CheckIncreaseLevelRequest(user: User)
case class CheckIncreaseLevelResult(user: User)

case class GetRightsAtLevelsRequest(user: User, levelFrom: Int, levelTo: Int)
case class GetRightsAtLevelsResult(rights: List[Rights])

case class GetLevelsForRightsRequest(user: User, functionality: List[String])
case class GetLevelsForRightsResult(levels: Map[Functionality.Value, Int])

case class SetDebugRequest(user: User, debug: String)
case class SetDebugResult(user: User)

case class SetGenderRequest(user: User, gender: Gender.Value)
case class SetGenderResult(user: User)

case class SetCityRequest(user: User, city: String)
case class SetCityResult(user: User)

case class SetCountryRequest(user: User, country: String)
case class SetCountryResult(allowed: ProfileModificationResult, user: Option[User])

case class GetCountryListRequest(user: User)
case class GetCountryListResult(countries: List[String])

case class UpdateUserCultureRequest(user: User)
case class UpdateUserCultureResult(user: User)


private[domain] trait ProfileAPI { this: DomainAPIComponent#DomainAPI with DBAccessor =>

  /**
   * Get iterator for all users.
   */
  def getAllUsers(request: GetAllUsersRequest): ApiResult[GetAllUsersResult] = handleDbException {
    OkApiResult(GetAllUsersResult(db.user.all))
  }

  /**
   * Reset all purchases (quests and themes) overnight.
   */
  def resetPurchases(request: ResetPurchasesRequest): ApiResult[ResetPurchasesResult] = handleDbException({
    import request._

    db.user.resetPurchases(user.id, user.getResetPurchasesTimeout)

    OkApiResult(ResetPurchasesResult())
  })

  /**
   * Adjust assets value and performs other modifications on profile because of this.
   */
  def adjustAssets(request: AdjustAssetsRequest): ApiResult[AdjustAssetsResult] = handleDbException {
    import request._

    Logger.debug("API - adjustAssets for user " + user.id)

    val del = reward.getOrElse(Assets()) - cost.getOrElse(Assets())

    val del2 = if ((del + user.profile.assets).rating < 0)
      Assets(del.coins, del.money, -user.profile.assets.rating)
    else
      del

    db.user.addToAssets(user.id, del2) ifSome { u =>
      checkIncreaseLevel(CheckIncreaseLevelRequest(u)) ifOk { r => OkApiResult(AdjustAssetsResult(r.user)) }
    }

  }

  /**
   * Check is user should increase its level and increases it if he should.
   */
  def checkIncreaseLevel(request: CheckIncreaseLevelRequest): ApiResult[CheckIncreaseLevelResult] = handleDbException {
    if (request.user.profile.ratingToNextLevel <= request.user.profile.assets.rating) {
      db.user.levelup(request.user.id, request.user.profile.ratingToNextLevel) ifSome { user =>
        db.user.setNextLevelRatingAndRights(
          user.id,
          user.ratingToNextLevel,
          user.calculateRights) ifSome { user =>
          OkApiResult(CheckIncreaseLevelResult(user))
        }
      }
    } else {
      OkApiResult(CheckIncreaseLevelResult(request.user))
    }
  }

  /**
   * Get rights for user if he would be at level.
   */
  def getRightsAtLevels(request: GetRightsAtLevelsRequest): ApiResult[GetRightsAtLevelsResult] = handleDbException {
    import request._

    val rights = for (l <- levelFrom to levelTo) yield {
      val u = user.copy(profile = user.profile.copy(publicProfile = user.profile.publicProfile.copy(level = l)))
      u.calculateRights
    }

    OkApiResult(GetRightsAtLevelsResult(rights.toList))
  }

  /**
   * Get level required to get a right.
   */
  def getLevelsForRights(request: GetLevelsForRightsRequest): ApiResult[GetLevelsForRightsResult] = handleDbException {
    val rv = constants.restrictions.filterKeys(request.functionality.contains(_))

    OkApiResult(GetLevelsForRightsResult(rv))
  }

  /**
   * Updates debug string in user profile.
   */
  def setDebug(request: SetDebugRequest): ApiResult[SetDebugResult] = handleDbException {
    import request._

    db.user.setDebug(user.id, debug) ifSome { v =>
      OkApiResult(SetDebugResult(v))
    }

  }

  /**
   * Updates user gender.
   */
  def setGender(request: SetGenderRequest): ApiResult[SetGenderResult] = handleDbException {
    import request._

    db.user.setGender(user.id, gender.toString) ifSome { v =>
      OkApiResult(SetGenderResult(v))
    }
  }

  /**
   * Updates user city.
   */
  def setCity(request: SetCityRequest): ApiResult[SetCityResult] = handleDbException {
    import request._

    db.user.setCity(user.id, city) ifSome { v =>
      OkApiResult(SetCityResult(v))
    }
  }

  /**
   * Updates user country.
   */
  def setCountry(request: SetCountryRequest): ApiResult[SetCountryResult] = handleDbException {
    import request._

    val countries = scala.io.Source.fromFile("conf/countries.txt").getLines().toList

    if (!countries.contains(country)) {
      OkApiResult(SetCountryResult(OutOfContent, None))
    } else {
      db.user.setCountry(user.id, country) ifSome { v =>
        updateUserCulture(UpdateUserCultureRequest(v)) ifOk { r =>
          OkApiResult(SetCountryResult(OK, Some(r.user)))
        }
      }
    }
  }

  /**
   * Get list of possible countries.
   */
  def getCountryList(request: GetCountryListRequest): ApiResult[GetCountryListResult] = handleDbException {
    val countries = scala.io.Source.fromFile("conf/countries.txt").getLines().toList

    OkApiResult(GetCountryListResult(countries))
  }

  /**
   * Update culture if country changed.
   */
  def updateUserCulture(request: UpdateUserCultureRequest): ApiResult[UpdateUserCultureResult] = handleDbException {

    request.user.profile.publicProfile.bio.country match {
      case Some(country) =>
        db.culture.findByCountry(country) match {
          case Some(c) =>
            request.user.demo.cultureId match {
              case Some(userC) =>
                if (c.id != userC)
                  db.user.updateCultureId(request.user.id, c.id)

              case None =>
                db.user.updateCultureId(request.user.id, c.id)
            }

          case None =>
            Logger.debug(s"Creating new culture ${request.user.profile.publicProfile.bio.country}")

            val newCulture = Culture(
              name = country,
              countries = List(country))
            db.culture.create(newCulture)
            db.user.updateCultureId(request.user.id, newCulture.id)
        }

      case None =>
        Logger.debug(s"Logging in user without a country")
    }

    OkApiResult(UpdateUserCultureResult(request.user))
  }
}

