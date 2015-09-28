package controllers.web.rest.component

import controllers.domain.app.user._
import controllers.web.helpers._

private object VoteBattleWSImplTypes {

  case class WSVoteBattleRequest(

    /**
     * id of battle we vote for.
     */
    battleId: String,

    /**
     * id of solution in the battle we vote for
     */
    solutionId: String)

  type WSVoteBattleResult = VoteBattleByUserResult
}

trait VoteBattleWSImpl extends BaseController with SecurityWSImpl with CommonFunctions { this: WSComponent#WS =>

  import controllers.web.rest.component.VoteBattleWSImplTypes._

  def voteBattle = wrapJsonApiCallReturnBody[WSVoteBattleResult] { (js, r) =>

    val v = Json.read[WSVoteBattleRequest](js)

    api.voteBattleByUser(VoteBattleByUserRequest(r.user, v.battleId, v.solutionId))
  }
}
