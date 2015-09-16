package controllers.web.rest.component

import controllers.domain.app.user._
import controllers.web.helpers._

private object ChallengesWSImplTypes {

  /**
   * Challenges are possible with Quest or Solution, not both.
   *
   * @param opponentId id of opponent to participate in battle.
   * @param myQuestId id of my quest to challenge opponent with.
   * @param mySolutionId id of my solution to challenge opponent with.
   */
  case class WSMakeChallengeRequest(
    opponentId: String,
    myQuestId: Option[String] = None,
    mySolutionId: Option[String] = None)
  type WSMakeChallengeResult = MakeChallengeResult

  /**
   * We should be owner or opponent in the challenge.
   *
   * @param challengeId Id of challenge to request.
   */
  case class WSGetChallengeRequest(
    challengeId: String)
  type WSGetChallengeResult = GetChallengeResult


  /**
   * Request for challenges our user created.
   *
   * @param statuses List of statuses to use in request.
   * @param pageNumber Number of page in result, zero based.
   * @param pageSize Number of items on a page.
   */
  case class WSGetMyChallengesRequest(
    statuses: List[String],
    pageNumber: Int,
    pageSize: Int)
  type WSGetMyChallengesResult = GetMyChallengesResult

  /**
   * Request for challenges others gave us.
   *
   * @param statuses List of statuses to use in request.
   * @param pageNumber Number of page in result, zero based.
   * @param pageSize Number of items on a page.
   */
  case class WSGetChallengesToMeRequest(
    statuses: List[String],
    pageNumber: Int,
    pageSize: Int)
  type WSGetChallengesToMeResult = GetChallengesToMeResult


  /**
   * Response to challenge
   *
   * @param challengeId Id of challenge to responding user he response on.
   * @param accepted Is the challenge accepted.
   * @param solutionId Id of solution user has/made to accept the challenge.
   */
  case class WSRespondChallengeRequest(
    challengeId: String,
    accepted: Boolean,
    solutionId: Option[String] = None)
  type WSRespondChallengeResult = RespondChallengeResult
}

trait ChallengesWSImpl extends QuestController with SecurityWSImpl { this: WSComponent#WS =>

  import ChallengesWSImplTypes._


  /**
   * Challenge someone.
   */
  def makeChallenge = wrapJsonApiCallReturnBody[WSMakeChallengeResult] { (js, r) =>
    val v = Json.read[WSMakeChallengeRequest](js.toString)

    api.makeChallenge(MakeChallengeRequest(r.user, v.opponentId, v.myQuestId, v.mySolutionId))
  }

  /**
   * Get all our and to us battle requests.
   */
  def getChallenge = wrapJsonApiCallReturnBody[WSGetChallengeResult] { (js, r) =>
    val v = Json.read[WSGetChallengeRequest](js.toString)

    api.getChallenge(GetChallengeRequest(r.user, v.challengeId))
  }

  /**
   * Get challenges made by requesting user.
   */
  def getMyChallenges = wrapJsonApiCallReturnBody[WSGetMyChallengesResult] { (js, r) =>
    val v = Json.read[WSGetMyChallengesRequest](js.toString)

    api.getMyChallenges(GetMyChallengesRequest(r.user, v.statuses, v.pageNumber, v.pageSize))
  }

  /**
   * Get challenges made to requesting user.
   */
  def getChallengesToMe = wrapJsonApiCallReturnBody[WSGetChallengesToMeResult] { (js, r) =>
    val v = Json.read[WSGetChallengesToMeRequest](js.toString)

    api.getChallengesToMe(GetChallengesToMeRequest(r.user, v.statuses, v.pageNumber, v.pageSize))
  }

  /**
   * Give a response on battle request.
   */
  def respondChallenge = wrapJsonApiCallReturnBody[WSRespondChallengeResult] { (js, r) =>
    val v = Json.read[WSRespondChallengeRequest](js.toString)

    api.respondChallenge(RespondChallengeRequest(r.user, v.challengeId, v.accepted, v.solutionId))
  }
}

