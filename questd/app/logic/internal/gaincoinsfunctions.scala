package logic.internal

import logic.constants._
import basefunctions._

object gaincoinsfunctions {
 
  def coinForVoteProposal(level: Int): Double = {
    val k = 471.066
    val d = 12.978
    val b = -872.55
    val y = 1.724E-4
    
    def coinForVoteProposalInt(level: Int, k: Double, d: Double, b: Double, y: Double): Double = {
      level match {
        case _ if level < voteQuestProposals => 0
        case _ if (level < voteReviews) && (level >= voteQuestProposals) => megaf(level, k, d, b, y)
        case _ => coinForVoteProposalInt(voteReviews - 1, k, d, b, y) * 0.63 + megaf(level, k, d, b, y) * 0.37
      }
    }

    coinForVoteProposalInt(level, k, d, b, y)
  }
}