package controllers.tasks.crawlers.base

import com.vita.akka.EasyRestartActor
import components._
import components.random.RandomComponent
import controllers.domain._
import logic.LogicBootstrapper
import play.Logger

case class StartWork()
case class Process[T](user: T)
case class EndWork()

abstract class BaseCrawler[T](apiPar: DomainAPIComponent#DomainAPI, randPar: RandomComponent#Random) extends EasyRestartActor
  with APIAccessor
  with RandomAccessor
  with LogicBootstrapper {

  val api: DomainAPIComponent#DomainAPI = apiPar
  val rand: RandomComponent#Random = randPar

  def receive = {
    case StartWork => start()
    case Process(o) => check(o.asInstanceOf[T])
    case EndWork => end()

    case a @ _ => Logger.error(s"Unknown event received: $a")
  }

  protected def start(): Unit = {}
  protected def check(user: T): Unit
  protected def end(): Unit = {}

}

