package controllers.web.rest.component

import controllers.web.rest.component.helpers._
import controllers.web.rest.protocol._
import controllers.domain.app.user._
import models.domain._
import _root_.helpers.rich._


private object ContentWSImplTypes
{
  case class WSGetOwnQuestsRequest(
    // see QuestStatus enum. if missing all solutions will be returned.
    status: List[String] = List(),

    // Number of page in result, zero based.
    pageNumber: Int,

    // Number of items on a page.
    pageSize: Int)
  type WSGetOwnQuestsResult = GetOwnQuestsResult


  case class WSGetOwnBattlesRequest(
    // see BattleStatus enum. if missing all solutions will be returned.
    status: List[String] = List(),

    // Number of page in result, zero based.
    pageNumber: Int,

    // Number of items on a page.
    pageSize: Int)
  type WSGetOwnBattlesResult = GetOwnBattlesResult


  case class WSGetQuestsForUserRequest(
    id: String,

    // see QuestSolutionStatus enum. if missing all solutions will be returned.
    status: List[String] = List(),

    // Number of page in result, zero based.
    pageNumber: Int,

    // Number of items on a page.
    pageSize: Int)
  type WSGetQuestsForUserResult = GetQuestsForUserResult


  case class WSGetSolutionsForUserRequest(
    id: String,

    // see QuestSolutionStatus enum. if missing all solutions will be returned.
    status: List[String] = List(),

    // Number of page in result, zero based.
    pageNumber: Int,

    // Number of items on a page.
    pageSize: Int)
  type WSGetSolutionsForUserResult = GetSolutionsForUserResult

  case class WSGetSolutionsForQuestRequest(
    id: String,

    // see QuestSolutionStatus enum. if missing all solutions will be returned.
    status: List[String] = List(),

    // Number of page in result, zero based.
    pageNumber: Int,

    // Number of items on a page.
    pageSize: Int)
  type WSGetSolutionsForQuestResult = GetSolutionsForQuestResult

  case class WSGetBattlesForUserRequest(
    id: String,

    // see BattleStatus enum. if missing all solutions will be returned.
    status: List[String] = List(),

    // Number of page in result, zero based.
    pageNumber: Int,

    // Number of items on a page.
    pageSize: Int)
  type WSGetBattlesForUserResult = GetBattlesForUserResult

  case class WSGetBattlesForSolutionRequest(
    id: String,

    // see QuestSolutionStatus enum. if missing all solutions will be returned.
    status: List[String] = List(),

    // Number of page in result, zero based.
    pageNumber: Int,

    // Number of items on a page.
    pageSize: Int)
  type WSGetBattlesForSolutionResult = GetBattlesForSolutionResult
}




trait ContentWSImpl extends QuestController with SecurityWSImpl with CommonFunctions { this: WSComponent#WS =>

  import ContentWSImplTypes._

  /**
   * Get quest by id.
   */
  def getQuests = wrapJsonApiCallReturnBody[WSGetQuestsResult] { (js, r) =>
    val v = Json.read[WSGetQuestsRequest](js)

    api.getQuests(GetQuestsRequest(r.user, v.ids))
  }

  /**
   * Get solution by id.
   */
  def getSolution = wrapJsonApiCallReturnBody[WSGetSolutionsResult] { (js, r) =>
    val v = Json.read[WSGetSolutionsRequest](js)

    api.getSolutions(GetSolutionsRequest(r.user, v.ids))
  }

  /**
   * Get battle by id
   * @return requested battle
   */
  def getBattle = wrapJsonApiCallReturnBody[WSGetBattlesResult] { (js, r) =>
    val v = Json.read[WSGetBattlesRequest](js)

    api.getBattles(GetBattlesRequest(r.user, v.ids))
  }

  /**
   * Get profile by id.
   */
  def getPublicProfiles = wrapJsonApiCallReturnBody[WSGetPublicProfileResult] { (js, r) =>
    val v = Json.read[WSGetPublicProfilesRequest](js)

    api.getPublicProfiles(GetPublicProfilesRequest(r.user, v.ids))
  }

  /**
   * Get own quests.
   */
  def getOwnQuests = wrapJsonApiCallReturnBody[WSGetOwnQuestsResult] { (js, r) =>
    val v = Json.read[WSGetOwnQuestsRequest](js)

    api.getOwnQuests(GetOwnQuestsRequest(
      r.user,
      v.status.map(QuestStatus.withNameEx),
      v.pageNumber,
      v.pageSize))
  }

  /**
   * Get own solutions.
   */
  def getOwnSolutions = wrapJsonApiCallReturnBody[WSGetOwnSolutionsResult] { (js, r) =>
    val v = Json.read[WSGetOwnSolutionsRequest](js)

    api.getOwnSolutions(GetOwnSolutionsRequest(
      r.user,
      v.status.map(SolutionStatus.withNameEx),
      v.pageNumber,
      v.pageSize))
  }

  /**
   * Get own battles.
   */
  def getOwnBattles = wrapJsonApiCallReturnBody[WSGetOwnBattlesResult] { (js, r) =>
    val v = Json.read[WSGetOwnBattlesRequest](js)

    api.getOwnBattles(GetOwnBattlesRequest(
      r.user,
      v.status.map(BattleStatus.withNameEx),
      v.pageNumber,
      v.pageSize))
  }

  /**
   * Get quests for a given person.
   */
  def getQuestsForUser = wrapJsonApiCallReturnBody[WSGetQuestsForUserResult] { (js, r) =>
    val v = Json.read[WSGetQuestsForUserRequest](js)

    api.getQuestsForUser(GetQuestsForUserRequest(
      r.user,
      v.id,
      v.status.map(QuestStatus.withNameEx),
      v.pageNumber,
      v.pageSize))
  }

  /**
   * Get solutions for a given person.
   */
  def getSolutionsForUser = wrapJsonApiCallReturnBody[WSGetSolutionsForUserResult] { (js, r) =>
    val v = Json.read[WSGetSolutionsForUserRequest](js)

    api.getSolutionsForUser(GetSolutionsForUserRequest(
      r.user,
      v.id,
      v.status.map(SolutionStatus.withNameEx),
      v.pageNumber,
      v.pageSize))
  }

  /**
   * Get solutions for a given quest id.
   */
  def getSolutionsForQuest = wrapJsonApiCallReturnBody[WSGetSolutionsForQuestResult] { (js, r) =>
    val v = Json.read[WSGetSolutionsForQuestRequest](js)

    api.getSolutionsForQuest(GetSolutionsForQuestRequest(
      r.user,
      v.id,
      v.status.map(SolutionStatus.withNameEx),
      v.pageNumber,
      v.pageSize))
  }

  /**
   * Get battles for a given person.
   */
  def getBattlesForUser = wrapJsonApiCallReturnBody[WSGetBattlesForUserResult] { (js, r) =>
    val v = Json.read[WSGetBattlesForUserRequest](js)

    api.getBattlesForUser(GetBattlesForUserRequest(
      r.user,
      v.id,
      v.status.map(BattleStatus.withNameEx),
      v.pageNumber,
      v.pageSize))
  }

  /**
   * Returns battles of a solution.
   */
  def getBattlesForSolution = wrapJsonApiCallReturnBody[WSGetBattlesForSolutionResult] { (js, r) =>
    val v = Json.read[WSGetBattlesForSolutionRequest](js)

    api.getBattlesForSolution(GetBattlesForSolutionRequest(
      r.user,
      v.id,
      v.status.map(BattleStatus.withNameEx),
      v.pageNumber,
      v.pageSize))
  }
}

