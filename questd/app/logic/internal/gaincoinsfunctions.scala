package logic.internal

import logic.constants._
import basefunctions._
import models.domain.Functionality._

object gaincoinsfunctions {

  def coinForVoteProposal(level: Int): Double = {
    val k = 471.066
    val d = 12.978
    val b = -872.55
    val y = 1.724E-4

    def coinForVoteProposalInt(level: Int, k: Double, d: Double, b: Double, y: Double): Double = {
      level match {
        case _ if level < levelFor(VoteQuests) => 0
        case _ if (level < levelFor(VoteReviews)) && (level >= levelFor(VoteQuests)) => megaf(level, k, d, b, y)
        case _ => coinForVoteProposalInt(levelFor(VoteReviews) - 1, k, d, b, y) * 0.63 + megaf(level, k, d, b, y) * 0.37
      }
    }

    coinForVoteProposalInt(level, k, d, b, y)
  }


  def coinForVoteResult(level: Int): Double = {
    val k = 162.15924
    val d = 4.593018
    val b = -150.641173
    val y = 4.989512E-9

    def coinForVoteResultInt(level: Int, k: Double, d: Double, b: Double, y: Double): Double = {
      level match {
        case _ if level < levelFor(VoteQuestSolutions) => 0
        case _ if (level < levelFor(SubmitPhotoQuests)) && (level >= levelFor(VoteQuestSolutions)) => megaf(level, k, d, b, y)
        case _ => coinForVoteResultInt(levelFor(SubmitPhotoQuests) - 1, k, d, b, y) * 0.90 + megaf(level, k, d, b, y) * 0.10
      }
    }

    coinForVoteResultInt(level, k, d, b, y)
  }

}

