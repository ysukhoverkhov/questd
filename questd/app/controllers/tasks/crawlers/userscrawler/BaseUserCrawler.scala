package controllers.tasks.crawlers.userscrawler

import play.Logger
import helpers.akka.EasyRestartActor
import controllers.domain._
import models.domain._
import logic.LogicBootstrapper
import components._
import components.random.RandomComponent

case class StartWork()
case class ProcessUser(user: User)
case class EndWork()

abstract class BaseUserCrawler(apiPar: DomainAPIComponent#DomainAPI, randPar: RandomComponent#Random) extends EasyRestartActor
  with APIAccessor
  with RandomAccessor
  with LogicBootstrapper {

  val api: DomainAPIComponent#DomainAPI = apiPar
  val rand: RandomComponent#Random = randPar

  def receive = {
    case StartWork => start()
    case ProcessUser(user: User) => check(user)
    case EndWork => end()

    case a @ _ => Logger.error("Unknown event received: " + a.toString)
  }

  protected def start(): Unit = {}
  protected def check(user: User): Unit
  protected def end(): Unit = {}

}

