package logic.user.util
import models.domain._
import models.store.mongo.helpers.listToSuperFlattenList
import logic.UserLogic

trait SelectionHelpers { this: UserLogic =>

  /**
   * Select quest what is not or quest and not in given list.
   */
  private[user] def selectQuest(
    i: Iterator[Quest],
    usedQuests: List[List[String]]): Option[Quest] = {

    selectObject[Quest, String](
      i,
      (_.id),
      (_.info.authorId),
      usedQuests,
      ((x: String) => x))
  }

  private[user] def selectQuestSolution(
    i: Iterator[QuestSolution],
    usedQuests: List[List[String]]): Option[QuestSolution] = {

    selectObject[QuestSolution, String](
      i,
      (_.id),
      (_.info.authorId),
      usedQuests,
      ((x: String) => x))
  }

  /**
   * Select object what is not our and not in list of lists.
   */
  private def selectObject[T, C](
    i: Iterator[T],
    getQuestId: (T => String),
    getQuestAthorId: (T => String),
    usedQuests: List[List[C]],
    getQuestIdInReference: (C => String)): Option[T] = {
    if (i.hasNext) {
      val q = i.next()

      if (getQuestAthorId(q) != user.id
        && !(listOfListsContainsString(usedQuests, getQuestIdInReference, getQuestId(q)))) {
        Some(q)
      } else {
        selectObject(i, getQuestId, getQuestAthorId, usedQuests, getQuestIdInReference)
      }
    } else {
      None
    }
  }

  /**
   * Check is string in list of dblists of strings.
   */
  def listOfListsContainsString[C](l: List[List[C]], getQuestIdInReference: (C => String), s: String) = {
    import models.store.mongo.helpers._
    l.mongoFlatten.map(getQuestIdInReference).contains(s)
  }
  
  /**
   * Runs algorithms in chain until one of them returns a value
   */
  private[util] def selectFromChain[T](chain: List[() => Option[T]], default: => T): T = {
    chain.
      foldLeft[Option[T]](None)((run, fun) => {
        if (run == None) fun() else run
      }).
      getOrElse(default)
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
        case Left(p) => {
          val curProbabiliy = p + fun._1
          if (curProbabiliy > dice) {
            Right(fun._2())
          } else {
            Left(curProbabiliy)
          }
        }
        case _ => run
      }
    }) match {
      case Right(oi) => Some(oi)
      case Left(_) => {
        None
      }
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
}
