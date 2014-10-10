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
  }
}

