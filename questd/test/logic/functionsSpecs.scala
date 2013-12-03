package logic

import org.specs2.mutable._
import org.specs2.runner._
import org.specs2.mock.Mockito
import org.junit.runner._
import play.Logger
import functions._
import org.specs2.matcher.BeEqualTo

class FunctionsSpecs extends Specification {

  "Functions should" should {

    "ratingForProposalAtLevel" in {
      ratingForProposalAtLevel(1) must beEqualTo(0)
      ratingForProposalAtLevel(11) must beEqualTo(0)
      ratingForProposalAtLevel(12) must beBetween(2240, 2250)
      ratingForProposalAtLevel(20) must beBetween(7630, 7640)
    }

    "questProposalPeriod" in {
      questProposalPeriod(12) must beEqualTo(9)
      questProposalPeriod(13) must beEqualTo(7)
      questProposalPeriod(20) must beEqualTo(2)
    }

    "costToSkipProposal" in {
      costToSkipProposal(12, 1) must beEqualTo(19)
      costToSkipProposal(12, 4) must beEqualTo(86)
      costToSkipProposal(20, 1) must beEqualTo(32 * 2)
      costToSkipProposal(20, 4) must beEqualTo(297)
    }
    
    "costToTakeQuestTheme" in {
      costToTakeQuestTheme(12) must beEqualTo(66 * 9)
      costToTakeQuestTheme(13) must beEqualTo(1002)
      costToTakeQuestTheme(20) must beEqualTo(1203 * 2)
    }

    "ratingToGiveUpQuestProposal" in {
      ratingToGiveUpQuestProposal(12) must beBetween(4360, 4370)
      ratingToGiveUpQuestProposal(20) must beBetween(15260, 15280)
    }
    
    
    "costToSkipQuest" in {
      costToSkipQuest(3, 1, 1) must beEqualTo(4)
      costToSkipQuest(3, 8, 1) must beEqualTo(28)
      costToSkipQuest(20, 1, 1) must beEqualTo(12)
      costToSkipQuest(20, 8, 1) must beEqualTo(87)
    }
    

    "costToTakeQuestToSolve" in {
      costToTakeQuestToSolve(3, 1) must beEqualTo(148)
      costToTakeQuestToSolve(11, 1) must beEqualTo(590)
      costToTakeQuestToSolve(12, 1) must beEqualTo(628)
      costToTakeQuestToSolve(20, 1) must beEqualTo(1296)
    }

    "ratingToGiveUpQuest" in {
      ratingToGiveUpQuest(3, 1) must beEqualTo(744)
      ratingToGiveUpQuest(11, 1) must beEqualTo(2301)
      ratingToGiveUpQuest(12, 1) must beEqualTo(2407)
      ratingToGiveUpQuest(20, 1) must beEqualTo(3732)
    }

    "rewardForVotingProposal" in {
      rewardForVotingProposal(10, 1) must beEqualTo(32)
      rewardForVotingProposal(10, 4) must beEqualTo(5)
      rewardForVotingProposal(15, 1) must beEqualTo(102)
      rewardForVotingProposal(15, 6) must beEqualTo(26)
      rewardForVotingProposal(20, 1) must beEqualTo(136)
      rewardForVotingProposal(20, 10) must beEqualTo(8)
    }

  }

}


