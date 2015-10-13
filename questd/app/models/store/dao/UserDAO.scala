package models.store.dao

import models.domain.user._
import models.store.dao.user._
  /**
   * Set user source for analytics
   *
   * @param id Id if user to set source to.
   * @param userSource Sourece of a user.
   */
  def setUserSource(id: String, userSource: String): Option[User]


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
