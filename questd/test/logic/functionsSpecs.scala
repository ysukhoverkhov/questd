package logic

import org.specs2.mutable._
import functions._

class functionsSpecs extends Specification {

  "Functions should" should {

    "questProposalPeriod" in {
      questProposalPeriod(12) must beEqualTo(7)
      questProposalPeriod(13) must beEqualTo(7)
      questProposalPeriod(20) must beEqualTo(7)
    }

    "coinSelectQuest" in {
      coinSelectQuest(3) must beEqualTo(100)
      coinSelectQuest(11) must beEqualTo(1280)
      coinSelectQuest(12) must beEqualTo(1628)
      coinSelectQuest(20) must beEqualTo(10000)
    }

    "ratingToLoseQuest" in {
      ratingToLoseQuest(3) must beEqualTo(75)
      ratingToLoseQuest(11) must beEqualTo(567)
      ratingToLoseQuest(12) must beEqualTo(709)
      ratingToLoseQuest(20) must beEqualTo(3800)
    }

    "rewardForVotingProposal" in {
      rewardForVotingProposal(10, 1) must beEqualTo(32)
      rewardForVotingProposal(10, 4) must beEqualTo(5)
      rewardForVotingProposal(15, 1) must beEqualTo(102)
      rewardForVotingProposal(15, 6) must beEqualTo(26)
      rewardForVotingProposal(20, 1) must beEqualTo(136)
      rewardForVotingProposal(20, 10) must beEqualTo(8)
    }

    "dailySalary" in {
      dailyCoinsSalary(1) must beEqualTo(12)
      dailyCoinsSalary(6) must beEqualTo(331)
      dailyCoinsSalary(10) must beEqualTo(398)
      dailyCoinsSalary(16) must beEqualTo(708)
      dailyCoinsSalary(20) must beEqualTo(1298)
    }

    "dailyRatingDecrease" in {
      dailyRatingDecrease(1) must beEqualTo(0)
      dailyRatingDecrease(3) must beEqualTo(302)
      dailyRatingDecrease(10) must beEqualTo(939)
      dailyRatingDecrease(20) must beEqualTo(4024)
    }

    "ratToGainLevel" in {
      ratToGainLevel(2) must beEqualTo(246)
      ratToGainLevel(10) must beEqualTo(10000)
      ratToGainLevel(20) must beEqualTo(500000)
    }

    "maxNumberOfFriendsOnLevel" in {
      maxNumberOfFriendsOnLevel(1) must beEqualTo(1)
      maxNumberOfFriendsOnLevel(2) must beEqualTo(1)
      maxNumberOfFriendsOnLevel(6) must beEqualTo(4)
      maxNumberOfFriendsOnLevel(10) must beEqualTo(10)
      maxNumberOfFriendsOnLevel(15) must beEqualTo(33)
      maxNumberOfFriendsOnLevel(20) must beEqualTo(100)
    }

    "costToFollowingPerson" in {
      costToFollowPerson(1) must beEqualTo(0)
      costToFollowPerson(7) must beEqualTo(0)
      costToFollowPerson(8) must beEqualTo(0)
      costToFollowPerson(10) must beEqualTo(0)
      costToFollowPerson(11) must beEqualTo(0)
      costToFollowPerson(20) must beEqualTo(0)
    }

    "costToInviteFriend" in {
      costToInviteFriend(1) must beEqualTo(6)
      costToInviteFriend(5) must beEqualTo(118)
      costToInviteFriend(6) must beEqualTo(165)
      costToInviteFriend(13) must beEqualTo(1030)
      costToInviteFriend(20) must beEqualTo(5000)
    }
  }

}


