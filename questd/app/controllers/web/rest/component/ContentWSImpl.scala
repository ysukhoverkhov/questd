package controllers.web.rest.component

import controllers.web.rest.component.helpers._
import controllers.web.rest.protocol._
import controllers.domain.app.user._
import models.domain._

trait ContentWSImpl extends QuestController with SecurityWSImpl with CommonFunctions { this: WSComponent#WS =>

  /**
   * Get quest by id.
   */
  def getQuest = wrapJsonApiCallReturnBody[WSGetQuestResult] { (js, r) =>
    val v = Json.read[WSGetQuestRequest](js)

    api.getQuest(GetQuestRequest(r.user, v.id))
  }

  /**
   * Get solution by id.
   */
  def getSolution = wrapJsonApiCallReturnBody[WSGetSolutionResult] { (js, r) =>
    val v = Json.read[WSGetSolutionRequest](js)

    api.getSolution(GetSolutionRequest(r.user, v.id))
  }

  /**
   * Get profile by id.
   */
  def getPublicProfiles = wrapJsonApiCallReturnBody[WSGetPublicProfileResult] { (js, r) =>
    val v = Json.read[WSGetPublicProfilesRequest](js)

    api.getPublicProfiles(GetPublicProfilesRequest(r.user, v.ids))
  }

  /**
   * Get own solutions.
   */
  def getOwnSolutions = wrapJsonApiCallReturnBody[WSGetOwnSolutionsResult] { (js, r) =>
    val v = Json.read[WSGetOwnSolutionsRequest](js)

    api.getOwnSolutions(GetOwnSolutionsRequest(
      r.user,
      v.status.map(SolutionStatus.withName),
      v.pageNumber,
      v.pageSize))
  }

  /**
   * Get own quests.
   */
  def getOwnQuests = wrapJsonApiCallReturnBody[WSGetOwnQuestsResult] { (js, r) =>
    val v = Json.read[WSGetOwnQuestsRequest](js)

    api.getOwnQuests(GetOwnQuestsRequest(
      r.user,
      v.status.map(QuestStatus.withName),
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
      v.status.map(SolutionStatus.withName),
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
      v.status.map(SolutionStatus.withName),
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
      v.status.map(QuestStatus.withName),
      v.pageNumber,
      v.pageSize))
  }
}

