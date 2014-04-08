package controllers.tasks.crawlers

import akka.actor.Actor
import akka.actor.Props
import play.Logger
import helpers.akka.EasyRestartActor
import controllers.tasks.messages.DoTask
import controllers.domain._
import controllers.domain.app.user._
import models.domain._
import controllers.tasks.crawlers.userscrawler._


object UsersWeeklyCrawler {
  def props(api: DomainAPIComponent#DomainAPI) = {
    Props(classOf[UsersWeeklyCrawler], api)
  }

  def name = "UsersWeeklyCrawler"
}

class UsersWeeklyCrawler(api: DomainAPIComponent#DomainAPI) extends BaseUsersScheduleCrawler(api) {

  protected val userActors = List(
      classOf[ShiftUserStats],
      classOf[CalculateProposalThreshold])
}

