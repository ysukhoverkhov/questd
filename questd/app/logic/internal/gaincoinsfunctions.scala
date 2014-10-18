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
    val k = 554.199
    val d = 12.978
    val b = -536.643
    val y = -8.684E-6

    def coinForVoteResultInt(level: Int, k: Double, d: Double, b: Double, y: Double): Double = {
      level match {
        case _ if level < levelFor(VoteQuestSolutions) => 0
        case _ if (level < levelFor(VoteQuests)) && (level >= levelFor(VoteQuestSolutions)) => megaf(level, k, d, b, y)
        case _ => coinForVoteResultInt(levelFor(VoteQuests) - 1, k, d, b, y) * 0.85 + megaf(level, k, d, b, y) * 0.15
      }
    }

    coinForVoteResultInt(level, k, d, b, y)
  }

}

