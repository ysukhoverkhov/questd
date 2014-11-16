package controllers.domain.app.user

import controllers.domain.app.quest.SelectQuestToTimeLineRequest
import models.domain._
import components._
import controllers.domain._
import controllers.domain.helpers._
import controllers.domain.helpers.PagerHelper._
import play.Logger


case class AddToTimeLineRequest(
  user: User,
  reason: TimeLineReason.Value,
  objectType: TimeLineType.Value,
  objectId: String,
  objectAuthorId: Option[String] = None)
case class AddToTimeLineResult(user: User)

case class AddToWatchersTimeLineRequest(
  user: User,
  reason: TimeLineReason.Value,
  objectType: TimeLineType.Value,
  objectId: String,
  objectAuthorId: Option[String] = None)
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
        objectAuthorId = objectAuthorId.getOrElse(user.id),
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
      user.friends.filter(_.status == FriendshipStatus.Accepted).map(_.friendId) ::: user.followers,
      TimeLineEntry(
        reason = reason,
        objectAuthorId = objectAuthorId.getOrElse(user.id),
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

    OkApiResult(GetTimeLineResult(user.timeLine.filter(
      _.ourVote match {
        case None => true
        case Some(ContentVote.Cool) => true
        case _ => false
      }
    ).iterator.drop(pageSize * pageNumber).take(pageSize).toList))
  }

  /**
   * Populates time line of user with random quests and solutions.
   */
  def populateTimeLineWithRandomThings(request: PopulateTimeLineWithRandomThingsRequest): ApiResult[PopulateTimeLineWithRandomThingsResult] = handleDbException {
    import request._

    Logger.trace(s"Populating time line for user ${user.id}")

    // BATCH
    val questsCount = config(api.ConfigParams.TimeLineRandomQuestsDaily).toInt
    val solutionsCount = config(api.ConfigParams.TimeLineRandomSolutionsDaily).toInt
    Logger.trace(s"  quests count = $questsCount")
    Logger.trace(s"  solutions count = $questsCount")

    def addRandomQuestsToTimeLine(user: Option[User], questsCount: Int): Option[User] = {
      user match {
        case None =>
          None
        case Some(u) =>
          if (questsCount == 0) {
            user
          } else {

            u.getRandomQuestForTimeLine match {
              case Some(q) =>
                Logger.trace(s"  random quest selected = ${q.id}")
                addToTimeLine(AddToTimeLineRequest(
                  user = u,
                  reason = TimeLineReason.Has,
                  objectType = TimeLineType.Quest,
                  objectId = q.id,
                  objectAuthorId = Some(q.info.authorId))) match {

                  case OkApiResult(res) =>
                    selectQuestToTimeLine(SelectQuestToTimeLineRequest(q))
                    addRandomQuestsToTimeLine(Some(res.user), questsCount - 1)
                  case _ =>
                    None
                }

              case None =>
                Logger.trace(s"  no quests found. Giving up")
                user
            }
          }
      }
    }

    def addRandomSolutionsToTimeLine(user: Option[User], solutionsCount: Int): Option[User] = {
      user match {
        case None =>
          None
        case Some(u) =>
          if (solutionsCount == 0) {
            user
          } else {

            u.getRandomSolutionForTimeLine match {
              case Some(s) =>
                Logger.trace(s"  random solution selected = ${s.id}")
                addToTimeLine(AddToTimeLineRequest(
                  user = u,
                  reason = TimeLineReason.Has,
                  objectType = TimeLineType.Solution,
                  objectId = s.id,
                  objectAuthorId = Some(s.info.authorId))) match {

                  case OkApiResult(res) =>
                    addRandomSolutionsToTimeLine(Some(res.user), solutionsCount - 1)
                  case _ =>
                    None
                }

              case None =>
                Logger.trace(s"  no solutions found. Giving up")
                user
            }
          }
      }
    }

    addRandomQuestsToTimeLine(Some(request.user), questsCount) ifSome { u =>
      addRandomSolutionsToTimeLine(Some(u), solutionsCount)
    }

    OkApiResult(PopulateTimeLineWithRandomThingsResult(user))
  }
}

