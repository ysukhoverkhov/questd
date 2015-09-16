package controllers.web.rest.component

import controllers.domain.app.user._
import controllers.web.helpers._

private object ChallengesWSImplTypes {

  case class WSMakeChallengeRequest(
    /// id of my solution to participate in battle.
    mySolutionId: String,

    /// id of opponent solution to participate in battle.
    opponentSolutionId: String
    )
  type WSMakeChallengeResult = MakeChallengeResult

//
//  type WSGetBattleRequestsResult = GetBattleRequestsResult
//
//
//  case class WSRespondBattleRequestRequest(
//    /// Id of solution we were challenged with.
//    opponentSolutionId: String,
//
//    /// Are we accept the challenge.
//    accepted: Boolean)
//  type WSRespondBattleRequestResult = RespondBattleRequestResult
}

trait ChallengesWSImpl extends QuestController with SecurityWSImpl { this: WSComponent#WS =>

  import ChallengesWSImplTypes._

//  def getChallenge = ws.getChallenge
//  def getMyChallenges = ws.getMyChallenges
//  def getChallengesToMe = ws.getChallengesToMe
//  def respondChallenge = ws.respondChallenge

  /**
   * Challenge someone.
   */
  def makeChallenge = wrapJsonApiCallReturnBody[WSMakeChallengeResult] { (js, r) =>
    val v = Json.read[WSMakeChallengeRequest](js.toString)

    api.makeChallenge(MakeChallengeRequest(r.user, v.mySolutionId, v.opponentSolutionId))
  }

  /**
   * Get all our and to us battle requests.
   */
  def getBattleRequests = wrapApiCallReturnBody[WSGetBattleRequestsResult] { r =>
    api.getBattleRequests(GetBattleRequestsRequest(r.user))
  }

  /**
   * Give a response on battle request.
   */
  def respondBattleRequest = wrapJsonApiCallReturnBody[WSRespondBattleRequestResult] { (js, r) =>
    val v = Json.read[WSRespondBattleRequestRequest](js.toString)

    api.respondBattleRequest(RespondBattleRequestRequest(r.user, v.opponentSolutionId, v.accepted))
  }
}

