package controllers.domain.app.user

import models.domain._
import models.store._
import play.Logger
import helpers._
import controllers.domain.helpers.exceptionwrappers._
import controllers.domain._
import components._
import controllers.domain.libs.facebook.UserFB

case class LoginFBRequest(userfb: UserFB, token: String)
case class LoginFBResult(session: String)

case class UserRequest(userId: Option[String] = None, sessionId: Option[String] = None)
case class UserResult(user: User)

private[domain] trait AuthAPI { this: DomainAPIComponent#DomainAPI with DBAccessor with FBAccessor =>

  /**
   * Login with FB. Or create new one if it doesn't exists.
   */
  def loginfb(params: LoginFBRequest): ApiResult[LoginFBResult] = handleDbException {

    def login(user: User) = {
      val uuid = java.util.UUID.randomUUID().toString()
      db.user.updateSessionId(user.id, uuid, params.token)

      // API Test place
      //      shiftStats(ShiftStatsRequest(user))
      //import controllers.domain.app.quest._
      //	  calculateProposalThresholds(CalculateProposalThresholdsRequest(10, 3))
      //      shiftHistory(ShiftHistoryRequest(user))

      OkApiResult(Some(LoginFBResult(uuid)))
    }

    def genderFromFBUser(u: UserFB) = {
      (u.getGender()) match {
        case "male" => Gender.Male
        case "female" => Gender.Female
        case _ => Gender.Unknown
      }
    }

    Logger.debug("Searching for user in database for login with fbid " + params.userfb.getId())

    db.user.readByFBid(params.userfb.getId()) match {
      case None => {

        Logger.debug("No user with FB id found, creating new one " + params.userfb.getId())

        val newUser = User(
          auth = AuthInfo(
            fbid = Some(params.userfb.getId()),
            fbtoken = Some(params.token)),
          profile = Profile(
            publicProfile = PublicProfile(
              bio = Bio(
                name = params.userfb.getFirstName(),
                gender = genderFromFBUser(params.userfb).toString,
                timezone = params.userfb.getTimezone().toInt,
                avatar = Some(
                  ContentReference(contentType = ContentType.Photo.toString, storage = "fb_avatar", reference = params.userfb.getId()))))))

        db.user.create(newUser)
        checkIncreaseLevel(CheckIncreaseLevelRequest(newUser))

        db.user.readByFBid(params.userfb.getId()) match {
          case None => {
            Logger.error("Unable to find user just created in DB with fbid " + params.userfb.getId())
            InternalErrorApiResult()
          }

          case Some(user) => {
            Logger.debug("New user with FB created " + user)

            login(user)
          }
        }

      }
      case Some(user) => {
        Logger.debug("Existing user login with FB " + user)

        login(user)
      }
    }
  }

  /**
   * User by session or id. At first we check session
   */
  def getUser(params: UserRequest): ApiResult[UserResult] = handleDbException {

    if (params.sessionId != None) {
      db.user.readBySessionId(params.sessionId.get) match {
        case None => NotAuthorisedApiResult()

        case Some(user: User) => OkApiResult(
          Some(UserResult(user)))
      }
    } else if (params.userId != null) {
      db.user.readById(params.userId.get) match {
        case None => NotFoundApiResult()

        case Some(user: User) => OkApiResult(
          Some(UserResult(user)))
      }

    } else {
      Logger.error("Wrong request for user.")
      InternalErrorApiResult()
    }

  }

}


