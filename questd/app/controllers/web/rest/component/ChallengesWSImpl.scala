package controllers.web.rest.component

import controllers.domain.app.user.{ChallengeBattleRequest, ChallengeBattleResult}
import controllers.web.helpers._

private object ChallengesWSImplTypes {

  case class WSChallengeBattleRequest(
    /// id of my solution to participate in battle.
    mySolutionId: String,

    /// id of opponent solution to participate in battle.
    opponentSolutionId: String
    )
  type WSChallengeBattleResult = ChallengeBattleResult

}

trait ChallengesWSImpl extends QuestController with SecurityWSImpl { this: WSComponent#WS =>

  import ChallengesWSImplTypes._

  /**
   * Challenge someone to a battle.
   *
   * @return
   */
  def challengeBattle = wrapJsonApiCallReturnBody[WSChallengeBattleResult] { (js, r) =>
    val v = Json.read[WSChallengeBattleRequest](js.toString)

    api.challengeBattle(ChallengeBattleRequest(r.user, v.mySolutionId, v.opponentSolutionId))
  }
}

