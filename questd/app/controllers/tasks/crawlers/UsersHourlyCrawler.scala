package controllers.tasks.crawlers

import akka.actor.Actor
import akka.actor.Props
import play.Logger
import helpers.akka.EasyRestartActor
import controllers.tasks.messages.DoTask
import controllers.domain._
import controllers.domain.app.user._
import controllers.tasks.crawlers.userscrawler._

import models.domain._


object UsersHourlyCrawler {
  def props(api: DomainAPIComponent#DomainAPI) = {
    Props(classOf[UsersHourlyCrawler], api)
  }

  def name = "UsersHourlyCrawler"
}

class UsersHourlyCrawler(api: DomainAPIComponent#DomainAPI) extends BaseUsersScheduleCrawler(api) {

  protected val userActors = List(
      classOf[CheckProposalDeadline],
      classOf[CheckQuestDeadline],
      classOf[ResetCountersAtNight],
      classOf[CheckShiftDailyResult])

}
