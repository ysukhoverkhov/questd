package controllers.domain.app.user

import controllers.domain.{BaseAPISpecs, OkApiResult}
import models.domain._
import models.domain.user.timeline.{TimeLineType, TimeLineReason}
import testhelpers.domainstubs._
import org.mockito.Matchers.{eq => mockEq}

class TimeLineAPISpecs extends BaseAPISpecs {

  "TimeLine API" should {

    "Add item to time line when requested" in context {
      val u = createUserStub()

      user.addEntryToTimeLine(any, any) returns Some(u)

      val result = api.addToTimeLine(AddToTimeLineRequest(
        user = u,
        reason = TimeLineReason.Created,
        objectType = TimeLineType.Quest,
        objectId = ""))

      result must beEqualTo(OkApiResult(AddToTimeLineResult(user = u)))
      there was one(user).addEntryToTimeLine(any, any)
    }

    "Removes item from time line when requested" in context {
      val u = createUserStub()
      val entryId = "lala"

      user.removeEntryFromTimeLineByObjectId(mockEq(u.id), mockEq(entryId)) returns Some(u)

      val result = api.removeFromTimeLine(RemoveFromTimeLineRequest(
        user = u,
        objectId = entryId))

      result must beEqualTo(OkApiResult(RemoveFromTimeLineResult(user = u)))
      there was one(user).removeEntryFromTimeLineByObjectId(mockEq(u.id), mockEq(entryId))
    }

    "Add item to friends' and followers' time line when requested" in context {
      val u = createUserStub()

      val result = api.addToWatchersTimeLine(AddToWatchersTimeLineRequest(
        user = u,
        reason = TimeLineReason.Created,
        objectType = TimeLineType.Quest,
        objectId = ""))

      result must beEqualTo(OkApiResult(AddToWatchersTimeLineResult(user = u)))
      there was one(user).addEntryToTimeLineMulti(any, any)
    }

    "getTimeLine should be able to limit result" in context {
      val entries = List(
        createTimeLineEntryStub(id = "1"),
        createTimeLineEntryStub(id = "2"),
        createTimeLineEntryStub(id = "3"),
        createTimeLineEntryStub(id = "4")
      )

      val u = createUserStub(timeLine = entries)

      val result = api.getTimeLine(GetTimeLineRequest(
        user = u,
        pageNumber = 0,
        pageSize = 20,
      untilEntryId = Some("3")))

      result must beEqualTo(OkApiResult(GetTimeLineResult(entries.slice(0,2))))
    }
  }
}

