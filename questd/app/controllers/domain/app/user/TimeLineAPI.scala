package controllers.domain.app.user

import components._
import controllers.domain._
import controllers.domain.app.quest.SelectQuestToTimeLineRequest
import controllers.domain.helpers.PagerHelper._
import controllers.domain.helpers._
import models.domain._
import play.Logger


case class AddToTimeLineRequest(
  user: User,
  reason: TimeLineReason.Value,
  objectType: TimeLineType.Value,
  objectId: String,
  actorId: Option[String] = None)
case class AddToTimeLineResult(user: User)

case class RemoveFromTimeLineRequest(
  user: User,
  objectId: String)
case class RemoveFromTimeLineResult(user: User)

case class AddToWatchersTimeLineRequest(
  user: User,
  reason: TimeLineReason.Value,
  objectType: TimeLineType.Value,
  objectId: String)
case class AddToWatchersTimeLineResult(user: User)

case class GetTimeLineRequest(
  user: User,
  pageNumber: Int,
  pageSize: Int,
  untilEntryId: Option[String] = None)
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
        actorId = actorId.getOrElse(user.id),
        objectType = objectType,
        objectId = objectId)) ifSome { u =>
      OkApiResult(AddToTimeLineResult(u))
    }
  }

  /**
   * Removes entry from time line.
   *
   */
  def removeFromTimeLine(request: RemoveFromTimeLineRequest): ApiResult[RemoveFromTimeLineResult] = handleDbException {
    import request._

    db.user.removeEntryFromTimeLineByObjectId(
      user.id,
      objectId) ifSome { u =>
      OkApiResult(RemoveFromTimeLineResult(u))
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
        actorId = user.id,
        objectType = objectType,
        objectId = objectId))

    OkApiResult(AddToWatchersTimeLineResult(user))
  }

  /**
   * Returns portion of time line.
   */
  def getTimeLine(request: GetTimeLineRequest): ApiResult[GetTimeLineResult] = handleDbException {

    val pageSize = adjustedPageSize(request.pageSize)
    val pageNumber = adjustedPageNumber(request.pageNumber)
// TODO: filter banned.
    OkApiResult(GetTimeLineResult(request.user.timeLine.iterator
      .drop(pageSize * pageNumber)
      .take(pageSize)
      .takeWhile(e => request.untilEntryId.fold(true)(id => e.id != id)).toList))
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
    val battlesCount = config(api.ConfigParams.TimeLineRandomBattlesDaily).toInt
    Logger.trace(s"  quests count = $questsCount")
    Logger.trace(s"  solutions count = $solutionsCount")
    Logger.trace(s"  battles count = $battlesCount")

    def addRandomQuestsToTimeLine(user: User, questsCount: Int): ApiResult[PopulateTimeLineWithRandomThingsResult] = {
      val quests = user.getRandomQuestsForTimeLine(questsCount)

      quests.foldLeft[ApiResult[PopulateTimeLineWithRandomThingsResult]](OkApiResult(PopulateTimeLineWithRandomThingsResult(user))) { (r, q) =>
        Logger.trace(s"  random quest selected = ${q.id}")

        r match {
          case OkApiResult(res) => {
            addToTimeLine(AddToTimeLineRequest(
              user = res.user,
              reason = TimeLineReason.Has,
              objectType = TimeLineType.Quest,
              objectId = q.id,
              actorId = Some(q.info.authorId)))
            } ifOk { res =>
              selectQuestToTimeLine(SelectQuestToTimeLineRequest(q))
            } ifOk {
              OkApiResult(PopulateTimeLineWithRandomThingsResult(res.user))
            }
          case _ =>
            r
        }
      }
    }

    def addRandomSolutionsToTimeLine(user: User, solutionsCount: Int): ApiResult[PopulateTimeLineWithRandomThingsResult] = {
      val solutions = user.getRandomSolutionsForTimeLine(solutionsCount)

      solutions.foldLeft[ApiResult[PopulateTimeLineWithRandomThingsResult]](OkApiResult(PopulateTimeLineWithRandomThingsResult(user))) { (r, s) =>
        Logger.trace(s"  random solution selected = ${s.id}")

        r match {
          case OkApiResult(res) => {
            addToTimeLine(AddToTimeLineRequest(
              user = res.user,
              reason = TimeLineReason.Has,
              objectType = TimeLineType.Solution,
              objectId = s.id,
              actorId = Some(s.info.authorId)))
          } ifOk { res =>
            OkApiResult(PopulateTimeLineWithRandomThingsResult(res.user))
          }
          case _ =>
            r
        }
      }
    }

    def addRandomBattlesToTimeLine(user: User, battlesCount: Int): ApiResult[PopulateTimeLineWithRandomThingsResult] = {
      val battles = user.getRandomBattlesForTimeLine(battlesCount)

      battles.foldLeft[ApiResult[PopulateTimeLineWithRandomThingsResult]](OkApiResult(PopulateTimeLineWithRandomThingsResult(user))) { (r, b) =>
        Logger.trace(s"  random battle selected = ${b.id}")

        r match {
          case OkApiResult(res) => {
            addToTimeLine(AddToTimeLineRequest(
              user = res.user,
              reason = TimeLineReason.Has,
              objectType = TimeLineType.Battle,
              objectId = b.id))
          } ifOk { res =>
            OkApiResult(PopulateTimeLineWithRandomThingsResult(res.user))
          }
          case _ =>
            r
        }
      }
    }

    {
      addRandomQuestsToTimeLine(request.user, questsCount)
    } ifOk { r =>
      addRandomSolutionsToTimeLine(r.user, solutionsCount)
    } ifOk { r =>
      addRandomBattlesToTimeLine(r.user, battlesCount)
    } ifOk { r =>
      db.user.setTimeLinePopulationTime(r.user.id, r.user.getPopulateTimeLineDate) ifSome { u =>
        OkApiResult(PopulateTimeLineWithRandomThingsResult(u))
      }
    }
  }
}

