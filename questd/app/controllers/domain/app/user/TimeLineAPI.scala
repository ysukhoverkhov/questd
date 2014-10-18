package controllers.domain.app.user

import models.domain._
import components._
import controllers.domain._
import controllers.domain.helpers._
import controllers.domain.helpers.PagerHelper._


case class AddToTimeLineRequest(
  user: User,
  reason: TimeLineReason.Value,
  objectType: TimeLineType.Value,
  objectId: String,
  entryAuthorId: Option[String] = None)
case class AddToTimeLineResult(user: User)

case class AddToWatchersTimeLineRequest(
  user: User,
  reason: TimeLineReason.Value,
  objectType: TimeLineType.Value,
  objectId: String)
case class AddToWatchersTimeLineResult(user: User)

case class GetTimeLineRequest(
  user: User,
  pageNumber: Int,
  pageSize: Int)
case class GetTimeLineResult(timeLine: List[TimeLineEntry])

case class PopulateTimeLineWithRandomThingsRequest(user: User)
case class PopulateTimeLineWithRandomThingsResult(user: User)

private[domain] trait TimeLineAPI { this: DomainAPIComponent#DomainAPI with DBAccessor =>

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
        entryAuthorId = entryAuthorId.getOrElse(user.id),
        objectType = objectType,
        objectId = objectId)) ifSome { u =>
      OkApiResult(AddToTimeLineResult(u))
    }
  }

  /**
   * Adds entry to time line of people who watch for us.
   */
  def addToWatchersTimeLine(request: AddToWatchersTimeLineRequest): ApiResult[AddToWatchersTimeLineResult] = handleDbException {
    import request._

    db.user.addEntryToTimeLineMulti(
      user.friends.map(_.friendId) ::: user.followers,
      TimeLineEntry(
        reason = reason,
        entryAuthorId = user.id,
        objectType = objectType,
        objectId = objectId))

    OkApiResult(AddToWatchersTimeLineResult(user))
  }

  /**
   * Returns portion of time line.
   */
  def getTimeLine(request: GetTimeLineRequest): ApiResult[GetTimeLineResult] = handleDbException {
    import request._

    val pageSize = adjustedPageSize(request.pageSize)
    val pageNumber = adjustedPageNumber(request.pageNumber)

    OkApiResult(GetTimeLineResult(user.timeLine.iterator.drop(pageSize * pageNumber).take(pageSize).toList))
  }

  /**
   * Populates time line of user with random quests and solutions.
   */
  def populateTimeLineWithRandomThings(request: PopulateTimeLineWithRandomThingsRequest): ApiResult[PopulateTimeLineWithRandomThingsResult] = handleDbException {
    import request._

    val questsCount = config(api.ConfigParams.TimeLineRandomQuestsDaily).toInt
    (1 to questsCount).foreach { x =>
      user.getRandomQuestForTimeLine match {
        case Some(q) =>
          addToTimeLine(AddToTimeLineRequest(
            user = user,
            reason = TimeLineReason.Has,
            objectType = TimeLineType.Quest,
            objectId = q.id,
            entryAuthorId = Some(q.info.authorId)))
        case None =>
      }
    }

    val solutionsCount = config(api.ConfigParams.TimeLineRandomSolutionsDaily).toInt
    (1 to solutionsCount).foreach { x =>
      user.getQuestSolutionForTimeLine match {
        case Some(s) =>
          addToTimeLine(AddToTimeLineRequest(
            user = user,
            reason = TimeLineReason.Has,
            objectType = TimeLineType.Solution,
            objectId = s.id,
            entryAuthorId = Some(s.info.authorId)))
        case None =>
      }
    }

    OkApiResult(PopulateTimeLineWithRandomThingsResult(user))
  }
}
