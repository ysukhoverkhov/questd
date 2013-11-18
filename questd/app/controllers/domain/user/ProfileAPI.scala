package controllers.domain.user

import models.domain._
import models.store._
import controllers.domain.DomainAPIComponent
import components._
import controllers.domain._
import controllers.domain.helpers.exceptionwrappers._

case class GetAllUsersRequest()
case class GetAllUsersResult(users: Iterator[User])

private [domain] trait ProfileAPI { this: DBAccessor => 
  
  /**
   * Get iterator for all users.
   */
  def getAllUsers(request: GetAllUsersRequest): ApiResult[GetAllUsersResult] = handleDbException {
    import request._

    OkApiResult(Some(GetAllUsersResult(db.user.all)))
  }

}

