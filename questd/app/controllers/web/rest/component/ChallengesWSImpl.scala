package controllers.web.rest.component

import com.vita.scala.extensions._
import controllers.domain.app.challenge._
import controllers.web.helpers._
import models.domain.challenge.ChallengeStatus

private object ChallengesWSImplTypes {

  /**
   * @param opponentId id of opponent to participate in battle.
   * @param myQuestId id of my quest to challenge opponent with.
   */
  case class WSMakeQuestChallengeRequest(
    opponentId: String,
    myQuestId: String)
  type WSMakeQuestChallengeResult = MakeQuestChallengeResult

  /**
   * @param opponentId id of opponent to participate in battle.
   * @param mySolutionId id of my solution to challenge opponent with.
   */
  case class WSMakeSolutionChallengeRequest(
    opponentId: String,
    mySolutionId: String)
  type WSMakeSolutionChallengeResult = MakeSolutionChallengeResult

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
  type WSRespondChallengeResult = AcceptChallengeResult
}

trait ChallengesWSImpl extends QuestController with SecurityWSImpl { this: WSComponent#WS =>

  import ChallengesWSImplTypes._


  /**
   * Challenge someone with quest.
   */
  def makeQuestChallenge = wrapJsonApiCallReturnBody[WSMakeQuestChallengeResult] { (js, r) =>
    val v = Json.read[WSMakeQuestChallengeRequest](js.toString)

    api.makeQuestChallenge(MakeQuestChallengeRequest(r.user, v.opponentId, v.myQuestId))
  }

  /**
   * Challenge someone with solution.
   */
  def makeSolutionChallenge = wrapJsonApiCallReturnBody[WSMakeSolutionChallengeResult] { (js, r) =>
    val v = Json.read[WSMakeSolutionChallengeRequest](js.toString)

    api.makeSolutionChallenge(MakeSolutionChallengeRequest(r.user, v.opponentId, v.mySolutionId))
  }

  /**
   * Get challenge by id if we are its part.
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

    api.getMyChallenges(GetMyChallengesRequest(r.user, v.statuses.map(ChallengeStatus.withNameEx), v.pageNumber, v.pageSize))
  }

  /**
   * Get challenges made to requesting user.
   */
  def getChallengesToMe = wrapJsonApiCallReturnBody[WSGetChallengesToMeResult] { (js, r) =>
    val v = Json.read[WSGetChallengesToMeRequest](js.toString)

    api.getChallengesToMe(GetChallengesToMeRequest(r.user, v.statuses.map(ChallengeStatus.withNameEx), v.pageNumber, v.pageSize))
  }

  /**
   * Give a response on battle request.
   */
  def respondChallenge = wrapJsonApiCallReturnBody[WSRespondChallengeResult] { (js, r) =>
    val v = Json.read[WSRespondChallengeRequest](js.toString)

    api.respondChallenge(AcceptChallengeRequest(r.user, v.challengeId, v.accepted, v.solutionId))
  }
}

