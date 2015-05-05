package logic

import org.specs2.mutable._
import functions._

class functionsSpecs extends Specification {

  "Functions should" should {

    "questCreationPeriod" in {
      questCreationPeriod(12) must beEqualTo(7)
      questCreationPeriod(13) must beEqualTo(7)
      questCreationPeriod(20) must beEqualTo(7)
    }

    "coinsToInviteFriendForVoteQuest" in {
      coinsToInviteFriendForVoteQuest(1) must beEqualTo(4)
      coinsToInviteFriendForVoteQuest(20) must beEqualTo(4)
    }

    "coinSelectQuest" in {
      coinSelectQuest(3) must beEqualTo(100)
      coinSelectQuest(11) must beEqualTo(1280)
      coinSelectQuest(12) must beEqualTo(1628)
      coinSelectQuest(20) must beEqualTo(10000)
    }

    "coinsToInviteFriendForVoteQuestSolution" in {
      coinsToInviteFriendForVoteQuestSolution(1) must beEqualTo(4)
      coinsToInviteFriendForVoteQuestSolution(20) must beEqualTo(4)
    }

    "ratingToLoseQuest" in {
      ratingToLoseQuest(3) must beEqualTo(75)
      ratingToLoseQuest(11) must beEqualTo(567)
      ratingToLoseQuest(12) must beEqualTo(709)
      ratingToLoseQuest(20) must beEqualTo(3800)
    }

    "ratingToWinQuest" in {
      ratingToWinQuest(3) must beEqualTo(150)
      ratingToWinQuest(11) must beEqualTo(1134)
      ratingToWinQuest(12) must beEqualTo(1418)
      ratingToWinQuest(20) must beEqualTo(7600)
    }

    "dailyQuestPassiveIncome" in {
      dailyQuestPassiveIncome must beEqualTo(50)
    }

    "dailyQuestIncomeForLikes" in {
      dailyQuestIncomeForLikes(0) must beEqualTo(0)
      dailyQuestIncomeForLikes(50) must beEqualTo(25)
      dailyQuestIncomeForLikes(200) must beEqualTo(100)
      dailyQuestIncomeForLikes(300) must beEqualTo(100)
    }

    "questIncomeForSolving" in {
      questIncomeForSolving must beEqualTo(25)
    }

    "dailyTasksCoinsSalary" in {
      dailyTasksCoinsSalary(1) must beEqualTo(12)
      dailyTasksCoinsSalary(6) must beEqualTo(331)
      dailyTasksCoinsSalary(10) must beEqualTo(398)
      dailyTasksCoinsSalary(16) must beEqualTo(708)
      dailyTasksCoinsSalary(20) must beEqualTo(1298)
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


