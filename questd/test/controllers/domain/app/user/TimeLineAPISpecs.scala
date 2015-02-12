package controllers.domain.app.user

import controllers.domain.{BaseAPISpecs, OkApiResult}
import models.domain._
import testhelpers.domainstubs._

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

    "getTimeLine should not return banned entries" in context {
      // TODO: update the test - test removing of banned things.

//      val entries = List(
//        createTimeLineEntryStub(objectType = TimeLineType.Quest),
//        createTimeLineEntryStub(objectType = TimeLineType.Quest)
//      )
//
//      val u = createUserStub(timeLine = entries, votedSolutions = Map(entries.head.id, ContentVote.Cheating))
//
//      val result = api.getTimeLine(GetTimeLineRequest(
//        user = u,
//        pageNumber = 0,
//        pageSize = 20))
//
//      result must beEqualTo(OkApiResult(GetTimeLineResult(entries.tail)))
      success
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

