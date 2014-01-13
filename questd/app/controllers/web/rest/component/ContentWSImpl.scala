package controllers.web.rest.component

import play.api._
import play.api.mvc._
import controllers.web.rest.component.helpers._
import controllers.web.rest.protocol._
import controllers.domain.app.user._

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
  def getProfileById = TODO

  /**
   * Get solutions for a given quest id.
   */
  def getSolutionsForQuest = TODO
  
  /**
   * Get solutions for a given person.
   */
  def getSolutionsForPerson = TODO
  
  /**
   * Get quests for a given person.
   */
  def getQuestsForPerson = TODO

}

