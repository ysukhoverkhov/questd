package controllers.domain.app.user

import java.util.Date

import controllers.domain.{BaseAPISpecs, OkApiResult}
import logic.UserLogic
import models.domain.user.message._
import models.domain.user.schedules.UserSchedules
import org.mockito.Matchers.{eq => mEq}
import testhelpers.domainstubs._


class EventsAPISpecs extends BaseAPISpecs {

  "Events API" should {

    "Give reward if everything is completed including tutorial" in {
      // I do not how to test function with tell to actor inside.
      success
    }

    "Send notification if there is a notification to send" in context {
      val u = createUserStub(messages = List(MessageAllTasksCompleted()))

      val userLogicMoc = mock[UserLogic]
      userLogicMoc.shouldSendNotification returns true
      api.user2Logic(any) returns userLogicMoc
      user.setNotificationSentTime(mEq(u.id), any) returns Some(u)
      doReturn(OkApiResult(NotifyWithMessageResult(u))).when(api).notifyWithMessage(any)

      val result = api.checkSendNotifications(CheckSendNotificationsRequest(u))

      there was one(api).notifyWithMessage(any)

      result must beAnInstanceOf[OkApiResult[CheckSendNotificationsResult]]
    }

    "Do not send notifications if there are not messages" in context {
      val u = createUserStub()

      val result = api.checkSendNotifications(CheckSendNotificationsRequest(u))

      there were no(user).setNotificationSentTime(mEq(u.id), any)
      there were no(api).notifyWithMessage(any)

      result must beAnInstanceOf[OkApiResult[CheckSendNotificationsResult]]
    }

    "Do not send notifications if time not come" in context {
      import com.github.nscala_time.time.Imports._
      import com.github.nscala_time.time._

      val u = createUserStub(
        messages = List(MessageAllTasksCompleted()),
        schedules = UserSchedules(
          lastNotificationSentAt = (DateTime.now + new RichInt(1).hour).toDate
        ))

      val result = api.checkSendNotifications(CheckSendNotificationsRequest(u))

      there were no(user).setNotificationSentTime(mEq(u.id), any)
      there were no(api).notifyWithMessage(any)

      result must beAnInstanceOf[OkApiResult[CheckSendNotificationsResult]]
    }

    "Sort notifications in priority order" in context {
      val messageAllTasksCompleted: Message = MessageAllTasksCompleted()
      val messageFriendshipAccepted: Message = MessageFriendshipAccepted("fid")
      val messageFriendshipRejected: Message = MessageFriendshipRejected("fid")

      val u = createUserStub(
        messages = List(messageAllTasksCompleted, messageFriendshipAccepted, messageFriendshipRejected))

      val userLogicMoc = mock[UserLogic]
      userLogicMoc.shouldSendNotification returns true
      api.user2Logic(any) returns userLogicMoc
      user.setNotificationSentTime(mEq(u.id), any) returns Some(u)
      doReturn(OkApiResult(NotifyWithMessageResult(u))).when(api).notifyWithMessage(any)

      val result = api.checkSendNotifications(CheckSendNotificationsRequest(u))

      there was one(api).notifyWithMessage(mEq(NotifyWithMessageRequest(u, messageFriendshipAccepted, 3)))

      result must beAnInstanceOf[OkApiResult[CheckSendNotificationsResult]]
    }

    "Do not resend notifications what already were sent" in context {
      val messageAllTasksCompleted: Message = (MessageAllTasksCompleted(): Message).copy(generatedAt = new Date(0))
      val messageFriendshipAccepted: Message = (MessageFriendshipAccepted("fid"): Message).copy(generatedAt = new Date(0))
      val messageFriendshipRejected: Message = (MessageFriendshipRejected("fid"): Message).copy(generatedAt = new Date(Long.MaxValue))

      val u = createUserStub(
        messages = List(messageAllTasksCompleted, messageFriendshipAccepted, messageFriendshipRejected),
        schedules = UserSchedules(lastNotificationSentAt = new Date()))

      val userLogicMoc = mock[UserLogic]
      userLogicMoc.shouldSendNotification returns true
      api.user2Logic(any) returns userLogicMoc
      user.setNotificationSentTime(mEq(u.id), any) returns Some(u)
      doReturn(OkApiResult(NotifyWithMessageResult(u))).when(api).notifyWithMessage(any)

      val result = api.checkSendNotifications(CheckSendNotificationsRequest(u))

      there was one(api).notifyWithMessage(mEq(NotifyWithMessageRequest(u, messageFriendshipRejected, 3)))

      result must beAnInstanceOf[OkApiResult[CheckSendNotificationsResult]]
    }
  }
}

