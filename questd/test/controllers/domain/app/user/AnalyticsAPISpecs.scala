package controllers.domain.app.user

import controllers.domain._
import testhelpers.domainstubs._

//noinspection ZeroIndexToHead
class AnalyticsAPISpecs extends BaseAPISpecs {

  "Analytics API" should {

    "Record user source for analytics" in context {
      val me = createUserStub()
      val source = "sourece"

      db.user.setUserSource(any, any) returns Some(me)

      val result = api.setUserSource(
        SetUserSourceRequest(
          user = me,
          userSource = source))

      result must beAnInstanceOf[OkApiResult[SetUserSourceResult]]

      there was one(user).setUserSource(any, any)
    }

  }
}

