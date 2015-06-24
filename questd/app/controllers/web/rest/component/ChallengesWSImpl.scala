package controllers.web.rest.component

import controllers.domain.app.user._
import controllers.web.helpers._

private object ChallengesWSImplTypes {

  case class WSChallengeBattleRequest(
    /// id of my solution to participate in battle.
    mySolutionId: String,

    /// id of opponent solution to participate in battle.
    opponentSolutionId: String
    )
  type WSChallengeBattleResult = ChallengeBattleResult


  type WSGetBattleRequestsResult = GetBattleRequestsResult


  case class WSRespondBattleRequestRequest(
    /// Id of solution we were challenged with.
    opponentSolutionId: String,

    /// Are we accept the challenge.
    accepted: Boolean)
  type WSRespondBattleRequestResult = RespondBattleRequestResult
}

trait ChallengesWSImpl extends QuestController with SecurityWSImpl { this: WSComponent#WS =>

  import ChallengesWSImplTypes._

  /**
   * Challenge someone to a battle.
   */
  def challengeBattle = wrapJsonApiCallReturnBody[WSChallengeBattleResult] { (js, r) =>
    val v = Json.read[WSChallengeBattleRequest](js.toString)

    api.challengeBattle(ChallengeBattleRequest(r.user, v.mySolutionId, v.opponentSolutionId))
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

