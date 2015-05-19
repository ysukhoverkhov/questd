package controllers.domain.app.user

import components._
import controllers.domain.app.protocol.ProfileModificationResult._
import controllers.domain.helpers._
import controllers.domain.{DomainAPIComponent, _}
import controllers.sn.client.{User => SNUser}
import logic.constants
import models.domain.common.Assets
import models.domain.culture.Culture
import models.domain.user._
import models.domain.user.auth.CrossPromotedApp
import play.{Logger, Play}

case class GetAllUsersRequest()
case class GetAllUsersResult(users: Iterator[User])

// TODO: get rid of reward and cost here.
case class AdjustAssetsRequest(user: User, reward: Option[Assets] = None, cost: Option[Assets] = None)
case class AdjustAssetsResult(user: User)

case class CheckIncreaseLevelRequest(user: User)
case class CheckIncreaseLevelResult(user: User)

case class GetRightsAtLevelsRequest(user: User, levelFrom: Int, levelTo: Int)
case class GetRightsAtLevelsResult(rights: List[Rights])

case class GetLevelsForRightsRequest(user: User, functionality: List[Functionality.Value])
case class GetLevelsForRightsResult(levels: Map[String, Int])

case class SetDebugRequest(user: User, debug: String)
case class SetDebugResult(allowed: ProfileModificationResult, profile: Option[Profile] = None)

case class SetGenderRequest(user: User, gender: Gender.Value)
case class SetGenderResult(allowed: ProfileModificationResult, profile: Option[Profile] = None)

case class SetCityRequest(user: User, city: String)
case class SetCityResult(allowed: ProfileModificationResult, profile: Option[Profile] = None)

case class SetCountryRequest(user: User, country: String)
case class SetCountryResult(allowed: ProfileModificationResult, profile: Option[Profile] = None)

case class GetCountryListRequest(user: User)
case class GetCountryListResult(countries: List[String])

case class UpdateCrossPromotionRequest(
  user: User,
  snUser: SNUser)
case class UpdateCrossPromotionResult(user: User)

case class UpdateUserCultureRequest(user: User)
case class UpdateUserCultureResult(user: User)

case class SetLevelDebugRequest(user: User, level: Int)
case class SetLevelDebugResult(user: User)

private[domain] trait ProfileAPI { this: DomainAPIComponent#DomainAPI with DBAccessor =>

  /**
   * Get iterator for all users.
   */
  def getAllUsers(request: GetAllUsersRequest): ApiResult[GetAllUsersResult] = handleDbException {
    OkApiResult(GetAllUsersResult(db.user.all))
  }

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
      checkIncreaseLevel(CheckIncreaseLevelRequest(u)) map { r => OkApiResult(AdjustAssetsResult(r.user)) }
    }
  }

  /**
   * Check is user should increase its level and increases it if he should.
   */
  def checkIncreaseLevel(request: CheckIncreaseLevelRequest): ApiResult[CheckIncreaseLevelResult] = handleDbException {
    if (request.user.profile.ratingToNextLevel <= request.user.profile.assets.rating) {
      db.user.levelUp(request.user.id, request.user.profile.ratingToNextLevel) ifSome { user =>
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
    val rv = constants.restrictions.filterKeys(f => request.functionality.contains(f)).map(r => r._1.toString -> r._2)

    OkApiResult(GetLevelsForRightsResult(rv))
  }

  /**
   * Updates debug string in user profile.
   */
  def setDebug(request: SetDebugRequest): ApiResult[SetDebugResult] = handleDbException {
    import request._

    db.user.setDebug(user.id, debug) ifSome { v =>
      OkApiResult(SetDebugResult(OK, Some(v.profile)))
    }

  }

  /**
   * Updates user gender.
   */
  def setGender(request: SetGenderRequest): ApiResult[SetGenderResult] = handleDbException {
    import request._

    db.user.setGender(user.id, gender.toString) ifSome { v =>
      OkApiResult(SetGenderResult(OK, Some(v.profile)))
    }
  }

  /**
   * Updates user city.
   */
  def setCity(request: SetCityRequest): ApiResult[SetCityResult] = handleDbException {
    import request._

    db.user.setCity(user.id, city) ifSome { v =>
      OkApiResult(SetCityResult(OK, Some(v.profile)))
    }
  }

  /**
   * Updates user country.
   */
  def setCountry(request: SetCountryRequest): ApiResult[SetCountryResult] = handleDbException {
    import request._

    val countries = scala.io.Source.fromFile(Play.application().getFile("conf/countries.txt"), "utf-8").getLines().toList

    if (!countries.contains(country)) {
      OkApiResult(SetCountryResult(OutOfContent, None))
    } else {
      db.user.setCountry(user.id, country) ifSome { v =>
        updateUserCulture(UpdateUserCultureRequest(v)) map { r =>
          OkApiResult(SetCountryResult(OK, Some(r.user.profile)))
        }
      }
    }
  }

  /**
   * Get list of possible countries.
   */
  def getCountryList(request: GetCountryListRequest): ApiResult[GetCountryListResult] = handleDbException {
    val countries = scala.io.Source.fromFile(Play.application().getFile("conf/countries.txt")).getLines().toList

    OkApiResult(GetCountryListResult(countries))
  }

  /**
   * Updates cross promotion info of current user.
   */
  def updateCrossPromotion(request: UpdateCrossPromotionRequest): ApiResult[UpdateCrossPromotionResult] = handleDbException {
    import request._

    val appsToAdd = request.snUser.idsInOtherApps.filter{ a =>
      request.user.auth.loginMethods.find(lp => lp.methodName == request.snUser.snName) match {
        case None =>
          Logger.error(s"LOgin method is not present in profile but we've just logged in with it: ${request.snUser.snName}")
          false
        case Some(lm) =>
          !lm.crossPromotion.apps.exists(storedApp => storedApp.appName == a.appName)
      }
    }.map{ a =>
      CrossPromotedApp(appName = a.appName, userId = a.snId)
    }

    (if (appsToAdd.nonEmpty)
      db.user.addCrossPromotions(
        id = user.id,
        snName = request.snUser.snName,
        apps = appsToAdd)
    else
      Some(user)) ifSome { u =>
        OkApiResult(UpdateCrossPromotionResult(u))
    }
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


  /**
   * Debug api for setting level of user.
   */
  def setLevelDebug(request: SetLevelDebugRequest): ApiResult[SetLevelDebugResult] = handleDbException {
    import request._

    val newLevel = user.copy(
      profile = user.profile.copy(
        publicProfile = user.profile.publicProfile.copy(
          level = level
        )
      ))

    val userWithNewRights = newLevel.copy(
      profile = user.profile.copy(
        rights = user.calculateRights,
        ratingToNextLevel = user.ratingToNextLevel
      ),
      privateDailyResults = List(
        DailyResult(
          user.getStartOfCurrentDailyResultPeriod)
      ))
    db.user.update(userWithNewRights)

    OkApiResult(SetLevelDebugResult(userWithNewRights))
  }
}

