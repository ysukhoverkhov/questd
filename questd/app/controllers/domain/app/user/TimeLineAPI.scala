package controllers.domain.app.user

import models.domain._
import components._
import controllers.domain._
import controllers.domain.helpers._

case class AddToTimeLineRequest(
  user: User,
  reason: TimeLineReason.Value,
  objectType: TimeLineType.Value,
  objectId: String)
case class AddToTimeLineResult(user: User)

case class AddToWatchersTimeLineRequest(user: User, entry: TimeLineEntry)
case class AddToWatchersTimeLineResult(user: User)

private[domain] trait TimeLineAPI { this: DBAccessor =>

  /**
   * Adds entry to time line.
   *
   */
  def addToTimeLine(request: AddToTimeLineRequest): ApiResult[AddToTimeLineResult] = handleDbException {
    import request._

    db.user.addEntryToTimeLine(
      user.id,
      TimeLineEntry(
        reason = reason,
        entryAuthorId = user.id,
        objectType = objectType,
        objectId = objectId)) ifSome { u =>
      OkApiResult(AddToTimeLineResult(u))
    }
  }

  /**
   * Adds entry to time line of people who watch for us.
   */
  // TODO: test me.
  // TODO: implement me.
  def addToWatchersTimeLine(request: AddToWatchersTimeLineRequest): ApiResult[AddToWatchersTimeLineResult] = handleDbException {
    import request._

    // 1. Do not add entry what is already in time line.
    // 2. add it.

    if (user.timeLine.map(_.objectId).contains(entry.objectId)) {
      OkApiResult(AddToWatchersTimeLineResult(user))
    } else {
      db.user.addEntryToTimeLine(user.id, entry) ifSome { u =>
        OkApiResult(AddToWatchersTimeLineResult(u))
      }
    }
  }

}

