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
import controllers.domain.app.quest.CalculateProposalThresholdsRequest


object CalculateProposalThreshold {
  def props(api: DomainAPIComponent#DomainAPI) = {
    Props(classOf[CalculateProposalThreshold], api)
  }

  def name = "CalculateProposalThreshold"

  @volatile var proposalsVoted: Double = 0;
  @volatile var proposalsLiked: Double = 0;
}

class CalculateProposalThreshold(api: DomainAPIComponent#DomainAPI) extends BaseUserCrawler(api) {

  
  protected override def start(): Unit = {
    CalculateProposalThreshold.proposalsVoted = 0;
    CalculateProposalThreshold.proposalsLiked = 0;
  }
  
  protected def check(user: User) = {
    if (user.userActive)
    {
      if (!user.stats.proposalsVotedPerDay.isNaN) {
        CalculateProposalThreshold.proposalsVoted += user.stats.proposalsVotedPerDay
      }
      if (!user.stats.proposalsLikedPerDay.isNaN) {
        CalculateProposalThreshold.proposalsLiked += user.stats.proposalsLikedPerDay
      }
    }
  }

  protected override def end(): Unit = {
    api.calculateProposalThresholds(CalculateProposalThresholdsRequest(CalculateProposalThreshold.proposalsVoted, CalculateProposalThreshold.proposalsLiked))
  }
}

