package logic

import scala.language.postfixOps

import components.componentregistry.ComponentRegistrySingleton
import play.Logger
import models.domain._

class QuestLogic(val quest: Quest) {

  lazy val api = ComponentRegistrySingleton.api

  /**
   * Check should quest change its status or should not.
   */
  // TODO implement me.
  def updateStatus: Quest = {

    // check for adding quest to rotation.

    // check for removing quest from rotation.

    // check for banning quest.

    // check for banning quest by time.

    {
      capPoints capPoints
    }.quest
  }

  private def capPoints: QuestLogic = {
    if (quest.rating.points > Int.MaxValue / 2)
      new QuestLogic(quest.copy(rating = quest.rating.copy(points = quest.rating.points / 2)))
    else
      this
  }

}

