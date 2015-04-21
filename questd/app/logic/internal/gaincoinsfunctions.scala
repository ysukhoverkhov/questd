package logic.internal

import logic.constants._
import basefunctions._
import models.domain.Functionality._

object gaincoinsfunctions {

  def coinForTasks(level: Int): Double = {
    val k = 162.15924
    val d = 4.593018
    val b = -150.641173
    val y = 4.989512E-9

    def coinForTasksInt(level: Int, k: Double, d: Double, b: Double, y: Double): Double = {
      level match {
        case _ if level < 1 => 0
        case _ if (level < levelFor(SubmitPhotoQuests)) && (level >= levelFor(VoteQuestSolutions)) => megaf(level, k, d, b, y)
        case _ => coinForTasksInt(levelFor(SubmitPhotoQuests) - 1, k, d, b, y) * 0.90 + megaf(level, k, d, b, y) * 0.10
      }
    }

    coinForTasksInt(level, k, d, b, y)
  }

}

