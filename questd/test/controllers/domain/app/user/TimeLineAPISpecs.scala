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
      val entries = List(
        createTimeLineEntryStub(ourVote = Some(ContentVote.Cheating)),
        createTimeLineEntryStub(ourVote = Some(ContentVote.Cool))
      )

      val u = createUserStub(timeLine = entries)

      val result = api.getTimeLine(GetTimeLineRequest(
        user = u,
        pageNumber = 0,
        pageSize = 20))

      result must beEqualTo(OkApiResult(GetTimeLineResult(entries.tail)))
    }

    "getTimeLine should be able to limit result" in context {
      val entries = List(
        createTimeLineEntryStub(id = "1", ourVote = Some(ContentVote.Cheating)),
        createTimeLineEntryStub(id = "2", ourVote = Some(ContentVote.Cool)),
        createTimeLineEntryStub(id = "3", ourVote = Some(ContentVote.Cool)),
        createTimeLineEntryStub(id = "4", ourVote = Some(ContentVote.Cool))
      )

      val u = createUserStub(timeLine = entries)

      val result = api.getTimeLine(GetTimeLineRequest(
        user = u,
        pageNumber = 0,
        pageSize = 20,
      untilEntryId = Some("3")))

      result must beEqualTo(OkApiResult(GetTimeLineResult(List(entries.tail.head))))
    }
  }
}

