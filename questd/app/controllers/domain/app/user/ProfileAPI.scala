package controllers.domain.app.user

import components._
import controllers.domain.app.protocol.ProfileModificationResult._
import controllers.domain.helpers._
import controllers.domain.{DomainAPIComponent, _}
import logic.constants
import models.domain.common.Assets
import models.domain.culture.Culture
import models.domain.user._
import models.domain.user.friends.ReferralStatus
import models.domain.user.message.MessageInformation
import models.domain.user.profile.{Functionality, Gender, Profile, Rights}
import play.{Logger, Play}

case class AdjustAssetsRequest(user: User, change: Assets)
case class AdjustAssetsResult(user: User)

case class CheckIncreaseLevelRequest(user: User)
case class CheckIncreaseLevelResult(user: User)

case class GetRightsAtLevelsRequest(user: User, levelFrom: Int, levelTo: Int)
case class GetRightsAtLevelsResult(rights: List[Rights])

case class GetLevelsForRightsRequest(user: User, functionality: List[Functionality.Value])
case class GetLevelsForRightsResult(levels: Map[String, Int])

case class SetGenderRequest(user: User, gender: Gender.Value)
case class SetGenderResult(allowed: ProfileModificationResult, profile: Option[Profile] = None)

case class SetCityRequest(user: User, city: String)
case class SetCityResult(allowed: ProfileModificationResult, profile: Option[Profile] = None)

case class SetCountryRequest(user: User, country: String)
case class SetCountryResult(allowed: ProfileModificationResult, profile: Option[Profile] = None)

case class UpdateUserCultureRequest(user: User)
case class UpdateUserCultureResult(user: User)

private[domain] trait ProfileAPI { this: DomainAPIComponent#DomainAPI with DBAccessor =>

  /**
   * Adjust assets value and performs other modifications on profile because of this.
   */
  def adjustAssets(request: AdjustAssetsRequest): ApiResult[AdjustAssetsResult] = handleDbException {
    import request._

    Logger.debug("API - adjustAssets for user " + user.id)

    val delta = if ((change + user.profile.assets).rating < 0)
      Assets(change.coins, change.money, -user.profile.assets.rating)
    else
      change

    db.user.addToAssets(user.id, delta) ifSome { u =>
      checkIncreaseLevel(CheckIncreaseLevelRequest(u)) map { r => OkApiResult(AdjustAssetsResult(r.user)) }
    }
  }

  /**
   * Check is user should increase its level and increases it if he should.
   */
  def checkIncreaseLevel(request: CheckIncreaseLevelRequest): ApiResult[CheckIncreaseLevelResult] = handleDbException {

    def rewardForFishingCrossPromotion(user: User): Unit = {
      val missing = "missing"
      def userFishingId(user: User): String = {
        user.auth.loginMethods.find(_.methodName == "FB")
          .fold(missing)(_.crossPromotion.apps.find(_.appName == "fishing_paradise").fold(missing)(_.userId))
      }

      val myIdInFishing = userFishingId(user)
      val referrerIdInFishing = user.friends.find(_.referralStatus == ReferralStatus.ReferredBy)
        .fold(missing)(f => db.user.readById(f.friendId).fold(missing)(userFishingId))

      Logger.error(
        s"User ${user.id} leveled up to level ${user.profile.publicProfile.level}. " +
          s"His fishing id is $myIdInFishing. " +
          s"His referrer fishing id is $referrerIdInFishing.")


      import play.api.Play.current
      import play.api.libs.json._
      import play.api.libs.ws._

      import scala.concurrent.ExecutionContext.Implicits.global
      import scala.concurrent.Future

      if (myIdInFishing != missing) {
        val data = Json.obj(
          "userId" -> s"fbk:$myIdInFishing",
          "shinersReward" -> s"${user.profile.publicProfile.level * 10}",
          "appName" -> "QuestMe",
          "appIconUrl" -> "https://web1.fishingparadise3d.com/www_promo_images/i/questme-1.jpg"
        )

        val futureResponse: Future[WSResponse] = WS.url("http://web1.fishingparadise3d.com/api/cross/giveReward").post(data)
//        val futureResponse: Future[WSResponse] = WS.url("http://webz.fishingparadise3d.com/api/cross/giveReward").post(data)

        futureResponse.onSuccess {
          case v if v.status == 200 =>
            Logger.debug(s"Successfully sent cross promotion for user ${user.id}")

            sendMessage(SendMessageRequest(
              user,
              MessageInformation(
                s"${user.profile.publicProfile.level * 10} Shiners were sent to Fishing Paradise 3d. " +
                  s"Launch Facebook version and get grab them!",
                None)))

          case v =>
            Logger.error(s"Unable to send cross promotion for user ${user.id}: ${v.status} - ${v.statusText}")
        }
        futureResponse.onFailure {
          case t =>
            Logger.error(s"Unable to send cross promotion for user ${user.id}")
        }
      } else {
        Logger.debug(s"Not sending request to FP3D since fishign id is missing for user ${user.id}")
      }
    }


    if (request.user.profile.ratingToNextLevel <= request.user.profile.assets.rating) {
      db.user.levelUp(request.user.id, request.user.profile.ratingToNextLevel) ifSome { user =>
        db.user.setNextLevelRatingAndRights(
          user.id,
          user.ratingToNextLevel,
          user.calculateRights) ifSome { user =>

          rewardForFishingCrossPromotion(user)

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

