package controllers.domain.admin

import components._
import controllers.domain._
import controllers.domain.helpers._

case class CleanUpObjectsRequest()
case class CleanUpObjectsResult()

private[domain] trait MaintenanceAdminAPI { this: DomainAPIComponent#DomainAPI with DBAccessor =>

  /**
   * Get config section by its name.
   */
  def cleanUpObjects(request: CleanUpObjectsRequest): ApiResult[CleanUpObjectsResult] = handleDbException {

    db.user.all.foreach { user =>
      db.user.update(user.initialized)
    }

    db.quest.all.foreach { quest =>
      db.quest.update(quest)
    }

    db.solution.all.foreach { solution =>
      db.solution.update(solution)
    }

    db.battle.all.foreach { battle =>
      db.battle.update(battle)
    }

    OkApiResult(CleanUpObjectsResult())
  }

}


