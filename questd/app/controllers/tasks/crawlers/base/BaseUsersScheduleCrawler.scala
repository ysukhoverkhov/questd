package controllers.tasks.crawlers.base

import components.random.RandomComponent
import controllers.domain._
import controllers.domain.app.user.{GetAllUsersResult, GetAllUsersRequest}
import models.domain._
import models.domain.user.User
import play.Logger


abstract class BaseUsersScheduleCrawler(
  api: DomainAPIComponent#DomainAPI,
  rand: RandomComponent#Random) extends BaseScheduleCrawler[User](api, rand) {

  override def allObjects: Iterator[User] = {
    api.getAllUsers(GetAllUsersRequest()) match {
      case OkApiResult(r: GetAllUsersResult) =>
        r.users

      case _ =>
        Logger.error(s"Unable to et all users from database")
        List.empty.iterator
    }
  }
}

