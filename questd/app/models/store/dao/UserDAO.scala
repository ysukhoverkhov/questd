package models.store.dao

import models.domain.user._
import models.store.dao.user._
import models.domain.user.timeline.{TimeLineEntry, TimeLineReason}
import models.domain.user.timeline.{TimeLineReason, TimeLineEntry}

trait UserDAO
  extends BaseDAO[User]
  with UserDepreciatedDAO
  with UserStatsDAO
  with UserDailyResultsDAO
  with UserFollowingDAO
  with UserBannedDAO
  with UserTimeLineDAO
  with UserTutorialDAO
  with UserTasksDAO
  with UserFriendsDAO
  with UserMessagesDAO
  with UserFetchDAO
  with UserContextsDAO
  with UserAuthDAO
  with UserProfileDAO
