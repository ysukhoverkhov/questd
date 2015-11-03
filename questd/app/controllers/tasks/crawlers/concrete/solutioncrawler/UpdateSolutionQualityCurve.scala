package controllers.tasks.crawlers.concrete.solutioncrawler

import akka.actor.Props
import components.random.RandomComponent
import controllers.domain._
import controllers.tasks.crawlers.base.BaseCrawler
import models.domain.crawlercontext.CrawlerContext
import models.domain.solution.Solution
import play.api.Logger

object UpdateSolutionQualityCurve {
  def props(api: DomainAPIComponent#DomainAPI, rand: RandomComponent#Random) = {
    Props(classOf[UpdateSolutionQualityCurve], api, rand)
  }

  def name = "UpdateSolutionQualityCurve"
}

class UpdateSolutionQualityCurve(
    apiPar: DomainAPIComponent#DomainAPI,
    randPar: RandomComponent#Random) extends BaseCrawler[Solution](apiPar, randPar)  {

  private val FramesCount = 100

  private sealed trait Data
  private case object Uninitialized extends Data
  private final case class Curve(curve: Array[Int]) extends Data

  private var data: Data = Uninitialized


  override protected def start(): Unit = {
    data = Curve(Array.ofDim[Int](FramesCount))
  }

  override protected def end(): Unit = {
    data match {
      case Uninitialized =>
      case Curve(curve) =>
        api.db.crawlerContext.upsert(
          CrawlerContext(
            id = "SolutionsCurve",
            params = curve.foldLeft[Map[String, String]](Map.empty){ (r, v) =>
              r + (r.size.toString -> v.toString)
            }
          )
        )
    }
  }

  // instead of voters count use here times it was selected to timeline and we are lack of this data for now so it should be added. It'll be also helpful for debugging.
  protected def check(solution: Solution) = {
    val ratio: Int = if (solution.rating.votersCount == 0) {
      0
    } else {
      (math.min(1, solution.rating.likesCount.toDouble / solution.rating.votersCount.toDouble) * (FramesCount - 1)).toInt
    }

    data match {
      case Uninitialized =>
      case d @ Curve(_) =>
        d.curve(ratio) = d.curve(ratio) + 1
    }
  }
}

