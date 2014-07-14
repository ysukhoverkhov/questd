package controllers.tasks.crawlers

import akka.actor.Actor
import akka.actor.Props
import play.Logger
import helpers.akka.EasyRestartActor
import controllers.tasks.messages.DoTask
import controllers.domain._
import controllers.domain.app.user._
import controllers.tasks.crawlers.userscrawler._
import components.random.RandomComponent

import models.domain._


object UsersHourlyCrawler {
  def props(api: DomainAPIComponent#DomainAPI, rand: RandomComponent#Random) = {
    Props(classOf[UsersHourlyCrawler], api, rand)
  }

  def name = "UsersHourlyCrawler"
}

class UsersHourlyCrawler(api: DomainAPIComponent#DomainAPI, rand: RandomComponent#Random) extends BaseUsersScheduleCrawler(api, rand) {

  protected val userActors = List(
      classOf[CheckProposalDeadline],
      classOf[CheckQuestDeadline],
      classOf[ResetCountersAtNight],
      classOf[CheckShiftDailyResult])
}
