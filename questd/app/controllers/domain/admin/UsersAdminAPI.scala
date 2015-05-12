package controllers.domain.admin

import models.domain.user.User
import play.Logger

import components.DBAccessor
import models.store._
import models.domain._
import controllers.domain.helpers._
import controllers.domain._

case class AllUsersRequest()
case class AllUsersResult(users: Iterator[User])

private[domain] trait UsersAdminAPI { this: DBAccessor =>

  /**
   * List all users
   */
  def allUsers(request: AllUsersRequest): ApiResult[AllUsersResult] = handleDbException {
    Logger.debug("Admin request for all users.")

    OkApiResult(AllUsersResult(db.user.all))
  }

}

