package controllers.tasks.crawlers

import akka.actor.Actor
import akka.actor.Props
import play.Logger
import helpers.akka.EasyRestartActor
import controllers.tasks.messages.DoTask


object DummyCrawler {
  def props = Props[DummyCrawler]
  def name = "DummyCrawler"
}

class DummyCrawler extends EasyRestartActor {

  def receive = {
    case DoTask => Logger.info("dum craw " + self.path.toString)
    case _ => Logger.info("asd")
  }

}

