package logic.user.util

import components.random.RandomComponent
import logic.constants

trait SelectionHelpers {/* this: UserLogic =>*/

  private [util] def getRandomObjects[T](count: Int, fun: (List[T]) => Option[T]): List[T] = {

    (1 to count).foldLeft[List[T]](List.empty) { (run, num) =>
      fun(run) match {
        case Some(b) =>
          b :: run
        case None =>
          run
      }
    }
  }

  /**
   * Check is string in list of dblists of strings.
   */
  private [user] def listOfListsContainsString[C](l: List[List[C]], getQuestIdInReference: (C => String), s: String) = {
    import models.store.mongo.helpers._
    l.mongoFlatten.map(getQuestIdInReference).contains(s)
  }

  /**
   * Runs algorithms in chain until one of them returns a value
   */
  private[util] def selectFromChain[T](chain: List[() => Option[T]]): Option[T] = {
    chain.
      foldLeft[Option[T]](None)((run, fun) => {
        if (run == None) fun() else run
      })
  }

  /**
   * Selects one of provided algorithms returning iterator according to weight and dice and ensures it returns not empty iterator.
   */
  private[util] def selectNonEmptyIteratorFromRandomAlgorithm[T](candidates: List[(Double, () => Option[Iterator[T]])], dice: Double): Option[Iterator[T]] = {
    valueWithWeightedProbability(candidates, dice) match {
      case None => None
      case Some(i) => checkNotEmptyIterator(i)
    }
  }

  /**
   * Select one of values with weights and dice.
   */
  private[util] def valueWithWeightedProbability[T](candidates: List[(Double, () => T)], dice: Double): Option[T] = {
    candidates.foldLeft[Either[Double, T]](Left(0))((run, fun) => {
      run match {
        case Left(p) =>
          val curProbability = p + fun._1
          if (curProbability > dice) {
            Right(fun._2())
          } else {
            Left(curProbability)
          }
        case _ => run
      }
    }) match {
      case Right(oi) => Some(oi)
      case Left(_) =>
        None
    }
  }

  /**
   * Check is iterator empty.
   */
  private[util] def checkNotEmptyIterator[T](i: Option[Iterator[T]]): Option[Iterator[T]] = {
    i match {
      case Some(it) => if (it.hasNext) i else None
      case None => None
    }
  }

  private[util] def levelsForNextObjectWithSigmaDistribution(mean: Float, sigma: Float, rand: RandomComponent#Random): (Int, Int) = {

    val v0 = rand.nextGaussian(mean, sigma)
    val v1 = Math.round(v0).toInt
    val v2 = if (v1 < 1) 1 else if (v1 > constants.MaxLevel) constants.MaxLevel else v1
    (v2, v2)
//    (Math.round(mean - sigma),
//      Math.round(mean + sigma))
  }
}
