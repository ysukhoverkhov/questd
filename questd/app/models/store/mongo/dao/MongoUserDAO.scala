package models.store.mongo.dao

import models.domain.user._
import models.store.dao._
import models.store.mongo.dao.user._
import models.store.mongo.helpers._


/**
 * DOA for User objects
 */
private[mongo] class MongoUserDAO
  extends BaseMongoDAO[User](collectionName = "users")
  with UserDAO
  with MongoUserDepreciatedDAO
  with MongoUserStatsDAO
  with MongoUserDailyResultsDAO
  with MongoUserFollowingDAO
  with MongoUserBannedDAO
  with MongoUserTimeLineDAO
  with MongoUserTutorialDAO
  with MongoUserTasksDAO
  with MongoUserFriendsDAO
  with MongoUserMessagesDAO
  with MongoUserFetchDAO
  with MongoUserContextsDAO
  with MongoUserAuthDAO
  with MongoUserProfileDAO



