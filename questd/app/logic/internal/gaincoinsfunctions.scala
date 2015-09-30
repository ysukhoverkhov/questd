package logic.internal

import logic.constants._
import logic.internal.basefunctions._
import models.domain.user.profile.Functionality._

object gaincoinsfunctions {

  def coinForTasks(level: Int): Double = {
    val k = 144.97295
    val d = 4.479141
    val b = -81.236775
    val y = 7.542141E-6

    def coinForTasksInt(level: Int, k: Double, d: Double, b: Double, y: Double): Double = {
      level match {
        case _ if level < 1 => 0
        case _ if (level < levelFor(SubmitPhotoQuests)) && (level >= levelFor(VoteSolutions)) => megaf(level, k, d, b, y)
        case _ => coinForTasksInt(levelFor(SubmitPhotoQuests) - 1, k, d, b, y) * 0.90 + megaf(level, k, d, b, y) * 0.10
      }
    }

    coinForTasksInt(level, k, d, b, y)
  }

}

