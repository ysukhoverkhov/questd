package controllers.domain.app.user

import components._
import controllers.domain._
import controllers.domain.app.battle.SelectBattleToTimeLineRequest
import controllers.domain.app.protocol.CommonCode
import controllers.domain.app.quest.SelectQuestToTimeLineRequest
import controllers.domain.app.solution.SelectSolutionToTimeLineRequest
import controllers.domain.helpers._
import models.domain.user._
import models.domain.user.friends.FriendshipStatus
import models.domain.user.profile.Profile
import models.domain.user.timeline.{TimeLineEntry, TimeLineReason, TimeLineType}
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


object HideFromTimeLineCode extends Enumeration with CommonCode {
  val EntryNotFound = Value
}
case class HideFromTimeLineRequest(
  user: User,
  entryId: String)
case class HideFromTimeLineResult(
  allowed: HideFromTimeLineCode.Value,
  profile: Option[Profile] = None)

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

case class PupulateTimeLineInitiallyRequest(user: User)
case class PupulateTimeLineInitiallyResult(user: User)

case class PopulateTimeLineWithRandomThingsRequest(user: User)
case class PopulateTimeLineWithRandomThingsResult(user: User)

private[domain] trait TimeLineAPI { this: DomainAPIComponent#DomainAPI with DBAccessor =>

  /**
   * Adds entry to time line. Does nothing is user has no culture.
   */
  def addToTimeLine(request: AddToTimeLineRequest): ApiResult[AddToTimeLineResult] = handleDbException {
    import request._

    Logger.trace(s"Adding to timeline reason = ${request.reason}, ${request.objectType}, objectId = ${request.objectId}, actorId = ${request.actorId}")

    if (user.timeLine.exists(_.objectId == objectId)) {
      OkApiResult(AddToTimeLineResult(user))
    } else {
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
  }

  /**
   * Removes entry from time line.
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
   * Hides entry from timeline. It's still on the server but is not returned in timeline requests.
   */
  def hideFromTimeLine(request: HideFromTimeLineRequest): ApiResult[HideFromTimeLineResult] = handleDbException {
    import HideFromTimeLineCode._
    import request._

    if (user.timeLine.exists(_.id == entryId)) {

      db.user.updateTimeLineEntry(
        id = request.user.id,
        entryId = request.entryId,
        reason = TimeLineReason.Hidden) ifSome { u =>

        OkApiResult(HideFromTimeLineResult(OK, Some(u.profile)))
      }
    } else {
      OkApiResult(HideFromTimeLineResult(EntryNotFound))
    }
  }

  /**
   * Adds entry to time line of people who watch for us.
   */
  def addToWatchersTimeLine(request: AddToWatchersTimeLineRequest): ApiResult[AddToWatchersTimeLineResult] = handleDbException {
    import request._

    val userIds = user.friends.filter(_.status == FriendshipStatus.Accepted).map(_.friendId) ::: user.followers

    userIds.foldLeft[ApiResult[AddToTimeLineResult]](OkApiResult(AddToTimeLineResult(user))){
      case (OkApiResult(_), friendId) =>
      db.user.readById(friendId).fold[ApiResult[AddToTimeLineResult]](OkApiResult(AddToTimeLineResult(user))) { friend =>
        addToTimeLine(AddToTimeLineRequest(
          user = friend,
          reason = reason,
          objectType = objectType,
          objectId = objectId,
          actorId = Some(user.id)))
      }
      case (result, _) =>
        result
    } map OkApiResult(AddToWatchersTimeLineResult(user))
  }

  /**
   * Returns portion of time line. Populates its with initial content if it's empty.
   */
  def getTimeLine(request: GetTimeLineRequest): ApiResult[GetTimeLineResult] = handleDbException {

    (if (request.user.timeLine.isEmpty) {
      populateTimeLineInitially(PupulateTimeLineInitiallyRequest(request.user))
    } else {
      OkApiResult(PupulateTimeLineInitiallyResult(request.user))
    }) map { r =>
      val pageSize = adjustedPageSize(request.pageSize)
      val pageNumber = adjustedPageNumber(request.pageNumber)

      OkApiResult(
        GetTimeLineResult(
          r.user.timeLine.iterator
            .filter(_.reason != TimeLineReason.Hidden)
            .slice(pageSize * pageNumber, pageSize * pageNumber + pageSize)
            .takeWhile(e => request.untilEntryId.fold(true)(id => e.id != id))
            .toList))
    }
  }

  /**
   * Internal call to populate timeline with initial things.
   */
  def populateTimeLineInitially(request: PupulateTimeLineInitiallyRequest): ApiResult[PupulateTimeLineInitiallyResult] = handleDbException {
    populateTimeLineWithRandomThings(PopulateTimeLineWithRandomThingsRequest(request.user)) map { r =>

      // Now adding to the top things we were invited with.
      r.user.friends.filter(_.referredWithContentId.nonEmpty).foreach { f =>
        db.quest.readById(f.referredWithContentId.get).fold[Option[TimeLineType.Value]] {
          db.solution.readById(f.referredWithContentId.get).fold[Option[TimeLineType.Value]] {
            None
          } { solution =>
            Some(TimeLineType.Solution)
          }
        } { quest =>
          Some(TimeLineType.Quest)
        }.fold() { contentType =>
          addToTimeLine(
            AddToTimeLineRequest(
              user = r.user,
              reason = TimeLineReason.Has,
              objectType = contentType,
              objectId = f.referredWithContentId.get,
              actorId = Some(f.friendId)
            ))
        }
      }

      OkApiResult(PupulateTimeLineInitiallyResult(r.user))
    }
  }

  /**
   * Populates time line of user with random quests and solutions.
   */
  def populateTimeLineWithRandomThings(request: PopulateTimeLineWithRandomThingsRequest): ApiResult[PopulateTimeLineWithRandomThingsResult] = handleDbException {
    import request._

    Logger.trace(s"Populating time line for user ${user.id}")

    def itemsCount(mean: Double, dev: Double, min: Double): Int = {
      math.round(rand.nextGaussian(mean, dev, min)).toInt
    }

    def questsCount: Int = {
      itemsCount(
        mean = if (user.timeLine.isEmpty)
          api.config(api.DefaultConfigParams.TimeLineRandomQuestsDailyMeanFirstTime).toDouble
        else
          api.config(api.DefaultConfigParams.TimeLineRandomQuestsDailyMean).toDouble,
        dev = api.config(api.DefaultConfigParams.TimeLineRandomQuestsDailyDeviation).toDouble,
        min = api.config(api.DefaultConfigParams.TimeLineRandomQuestsDailyMin).toDouble)
    }

    def solutionsCount: Int = {
      itemsCount(
        mean = if (user.timeLine.isEmpty)
          api.config(api.DefaultConfigParams.TimeLineRandomSolutionsDailyMeanFirstTime).toDouble
        else
          api.config(api.DefaultConfigParams.TimeLineRandomSolutionsDailyMean).toDouble,
        dev = api.config(api.DefaultConfigParams.TimeLineRandomSolutionsDailyDeviation).toDouble,
        min = api.config(api.DefaultConfigParams.TimeLineRandomSolutionsDailyMin).toDouble)
    }

    def battlesCount: Int = {
      if (user.timeLine.isEmpty) {
        api.config(api.DefaultConfigParams.TimeLineRandomBattlesDailyMeanFirstTime).toInt
      } else {
        itemsCount(
          mean = api.config(api.DefaultConfigParams.TimeLineRandomBattlesDailyMean).toDouble,
          dev = api.config(api.DefaultConfigParams.TimeLineRandomBattlesDailyDeviation).toDouble,
          min = api.config(api.DefaultConfigParams.TimeLineRandomBattlesDailyMin).toDouble)
      }
    }

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
            } map { res =>
              selectQuestToTimeLine(SelectQuestToTimeLineRequest(q)) map {
                OkApiResult(PopulateTimeLineWithRandomThingsResult(res.user))
              }
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
          } map { res =>
            selectSolutionToTimeLine(SelectSolutionToTimeLineRequest(s)) map {
              OkApiResult(PopulateTimeLineWithRandomThingsResult(res.user))
            }
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
          } map { res =>
            selectBattleToTimeLine(SelectBattleToTimeLineRequest(b)) map {
              OkApiResult(PopulateTimeLineWithRandomThingsResult(res.user))
            }
          }
          case _ =>
            r
        }
      }
    }

    if (request.user.bioComplete) {
      {
        addRandomBattlesToTimeLine(request.user, battlesCount)
      } map { r =>
        addRandomSolutionsToTimeLine(r.user, solutionsCount)
      } map { r =>
        addRandomQuestsToTimeLine(r.user, questsCount)
      } map { r =>
        db.user.setTimeLinePopulationTime(r.user.id, r.user.getPopulateTimeLineDate) ifSome { u =>
          OkApiResult(PopulateTimeLineWithRandomThingsResult(u))
        }
      }
    } else {
      OkApiResult(PopulateTimeLineWithRandomThingsResult(user))
    }
  }
}

