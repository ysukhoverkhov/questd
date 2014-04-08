package controllers.tasks.crawlers.userscrawler

import akka.actor.Actor
import akka.actor.Props
import play.Logger
import helpers.akka.EasyRestartActor
import controllers.tasks.messages.DoTask
import controllers.domain._
import controllers.domain.app.user._
import models.domain._
import java.util.Date
import logic._


object CalculateProposalThreshold {
  def props(api: DomainAPIComponent#DomainAPI) = {
    Props(classOf[CalculateProposalThreshold], api)
  }

  def name = "CalculateProposalThreshold"
}

class CalculateProposalThreshold(api: DomainAPIComponent#DomainAPI) extends BaseUserCrawler(api) {

  var maxVotes: Double = 0;
  var maxProposals: Double = 0;
  
  protected override def start(): Unit = {
    maxVotes = 0;
    maxProposals = 0;
    Logger.error("start")
  }
  
  protected def check(user: User) = {
    if (user.userActive) {
    }
    Logger.error("check")
  }

  protected override def end(): Unit = {
    
    // TODO store config section name in constant (check how we did it for WS config).
//    api.updateConfig("a" -> "b")
    Logger.error("end")
  }
}

