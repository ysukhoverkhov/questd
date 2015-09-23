package controllers.domain.app.user

import controllers.domain.{BaseAPISpecs, OkApiResult}
import models.domain.user.friends.{Friendship, FriendshipStatus}
import models.domain.user.timeline.{TimeLineEntry, TimeLineReason, TimeLineType}
import org.mockito.Matchers.{eq => mockEq}
import org.mockito.Mockito._
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

    "Do not add item to time line when it's already there" in context {
      val objectId = "oid"
      val u = createUserStub(timeLine = List(TimeLineEntry(
        reason = TimeLineReason.Created,
        actorId = "",
        objectType = TimeLineType.Battle,
        objectId = objectId)))

      user.addEntryToTimeLine(any, any) returns Some(u)

      val result = api.addToTimeLine(AddToTimeLineRequest(
        user = u,
        reason = TimeLineReason.Created,
        objectType = TimeLineType.Quest,
        objectId = objectId))

      result must beEqualTo(OkApiResult(AddToTimeLineResult(user = u)))
      there was no(user).addEntryToTimeLine(any, any)
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
      val friends = List(Friendship("fid1", FriendshipStatus.Accepted), Friendship("fid2", FriendshipStatus.Invited))
      val u = createUserStub(friends = friends, followers = List("1"))

      user.readById(any) returns Some(u)
      user.addEntryToTimeLine(any, any) returns Some(u)

      val result = api.addToWatchersTimeLine(AddToWatchersTimeLineRequest(
        user = u,
        reason = TimeLineReason.Created,
        objectType = TimeLineType.Quest,
        objectId = ""))

      result must beEqualTo(OkApiResult(AddToWatchersTimeLineResult(user = u)))
      there were two(user).addEntryToTimeLine(any, any)
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

    "getTimeLine filters out hidden" in context {
      val entries = List(
        createTimeLineEntryStub(id = "1"),
        createTimeLineEntryStub(id = "2"),
        createTimeLineEntryStub(id = "3", reason = TimeLineReason.Hidden),
        createTimeLineEntryStub(id = "4")
      )

      val u = createUserStub(timeLine = entries)

      val result = api.getTimeLine(GetTimeLineRequest(
        user = u,
        pageNumber = 0,
        pageSize = 20))

      result must beEqualTo(OkApiResult(GetTimeLineResult(entries.filter(_.reason != TimeLineReason.Hidden))))
    }

    "getTimeLine populates timeline if it's empty" in context {
      val u = createUserStub()

      doReturn(OkApiResult(PopulateTimeLineWithRandomThingsResult(u))).when(api).populateTimeLineWithRandomThings(any)

      val result = api.getTimeLine(GetTimeLineRequest(
        user = u,
        pageNumber = 0,
        pageSize = 20))

      result must beAnInstanceOf[OkApiResult[PopulateTimeLineWithRandomThingsResult]]
      there were one(api).populateTimeLineWithRandomThings(any)
    }


//    "populateTimeLineWithRandomThings populates it" in context {
//      val u = createUserStub()
//
//      val result = api.populateTimeLineWithRandomThings(PopulateTimeLineWithRandomThingsRequest(u))
//
//      result must beAnInstanceOf[OkApiResult[PopulateTimeLineWithRandomThingsResult]]
//    }
  }
}

