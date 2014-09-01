package logic.user

import models.domain._
import controllers.domain.OkApiResult
import models.domain.admin.ConfigSection
import controllers.domain.config._ConfigParams
import controllers.domain.app.questsolution._
import testhelpers.domainstubs._

class UserLogicSelectingSolutionSpecs extends BaseUserLogicSpecs {

  /**
   * Creates stub config for our tests.
   */
  private def createStubConfig = {
    api.ConfigParams returns _ConfigParams

    val config = mock[ConfigSection]

    config.apply(api.ConfigParams.SolutionProbabilityLevelsToGiveStartingSolutions) returns "5"
    config.apply(api.ConfigParams.SolutionProbabilityStartingVIPSolutions) returns "0.50"

    config.apply(api.ConfigParams.SolutionProbabilityFriends) returns "0.25"
    config.apply(api.ConfigParams.SolutionProbabilityShortlist) returns "0.25"
    config.apply(api.ConfigParams.SolutionProbabilityLiked) returns "0.20"
    config.apply(api.ConfigParams.SolutionProbabilityStar) returns "0.10"

    config
  }

  private def createFriend(newid: String) = {
    User(id = newid)
  }

  "User Logic solution selector" should {

    "Return solution from friends if dice rolls so" in {

      api.config returns createStubConfig
      rand.nextDouble returns 0.13

      val qid = "qid"

      api.getFriendsSolutions(any[GetFriendsSolutionsRequest]) returns OkApiResult(GetFriendsSolutionsResult(List(createSolutionStub(id = qid, userId = "author")).iterator))

      val u = User()
      val q = u.getRandomSolution

      there was one(rand).nextDouble
      there was one(api).getFriendsSolutions(any[GetFriendsSolutionsRequest])

      q must beSome.which(q => q.id == qid)
    }

    "Return solution from shortlist if dice rolls so" in {
      api.config returns createStubConfig
      rand.nextDouble returns 0.38

      val qid = "qid"

      api.getShortlistSolutions(any[GetShortlistSolutionsRequest]) returns OkApiResult(GetShortlistSolutionsResult(List(createSolutionStub(id = qid, userId = "author")).iterator))

      val u = User()
      val q = u.getRandomSolution

      there was one(rand).nextDouble
      there was one(api).getShortlistSolutions(any[GetShortlistSolutionsRequest])

      q must beSome.which(q => q.id == qid)
    }

    "Return liked quest if dice rolls so" in {
      api.config returns createStubConfig
      rand.nextDouble returns 0.58

      val qid = "qid"

      api.getSolutionsForLikedQuests(any[GetSolutionsForLikedQuestsRequest]) returns OkApiResult(GetSolutionsForLikedQuestsResult(List(createSolutionStub(id = qid, userId = "author")).iterator))

      val u = User()
      val q = u.getRandomSolution

      there was one(rand).nextDouble
      there was one(api).getSolutionsForLikedQuests(any[GetSolutionsForLikedQuestsRequest])

      q must beSome.which(q => q.id == qid)
    }

    "Return VIP solutions if dice rolls so" in {
      api.config returns createStubConfig
      rand.nextDouble returns 0.75

      val qid = "qid"

      api.getVIPSolutions(any[GetVIPSolutionsRequest]) returns OkApiResult(GetVIPSolutionsResult(List(createSolutionStub(id = qid, userId = "author")).iterator))

      val u = User()
      val q = u.getRandomSolution

      there was one(rand).nextDouble
      there was one(api).getVIPSolutions(any[GetVIPSolutionsRequest])

      q must beSome.which(q => q.id == qid)
    }

    "Return VIP solutions with favorite theme ids if dice rolls so" in {
      val qid = "qid"
      val u = User(
        profile = Profile(
          publicProfile = PublicProfile(level = 10)),
        history = UserHistory(
          themesOfSelectedQuests = List("1", "2", "3", "4")))

      api.config returns createStubConfig
      rand.nextDouble returns 0.75
      rand.nextInt(4) returns 0 thenReturns 1 thenReturns 2

      api.getVIPSolutions(GetVIPSolutionsRequest(u, QuestSolutionStatus.OnVoting, Some((-20, 12)), List("1", "2", "3"))) returns OkApiResult(GetVIPSolutionsResult(List(createSolutionStub(id = qid, userId = "author")).iterator))

      val q = u.getRandomSolution

      there was one(rand).nextDouble
      there were three(rand).nextInt(4)
      there was one(api).getVIPSolutions(GetVIPSolutionsRequest(u, QuestSolutionStatus.OnVoting, Some((-20, 12)), List("1", "2", "3")))

      q must beSome.which(q => q.id == qid)
    }

    "Return All solutions with favorite theme ids if dice rolls so" in {
      val qid = "qid"
      val u = User(
        history = UserHistory(
          themesOfSelectedQuests = List("1", "2", "3", "4")))

      api.config returns createStubConfig
      rand.nextDouble returns 0.95
      rand.nextInt(4) returns 1

      api.getAllSolutions(GetAllSolutionsRequest(u, QuestSolutionStatus.OnVoting, Some((-12, 20)), List("2"))) returns OkApiResult(GetAllSolutionsResult(List(createSolutionStub(id = qid, userId = "author")).iterator))

      val q = u.getRandomSolution

      there was one(rand).nextDouble
      there was one(rand).nextInt(4)
      there was one(api).getAllSolutions(GetAllSolutionsRequest(u, QuestSolutionStatus.OnVoting, Some((-12, 20)), List("2")))

      q must beSome.which(q => q.id == qid)
    }

    "Starting solutions return vip solutions" in {
      val qid = "qid"
      val u = User(
        profile = Profile(
          publicProfile = PublicProfile(level = 1)))

      api.config returns createStubConfig
      rand.nextDouble returns 0.0

      api.getVIPSolutions(any[GetVIPSolutionsRequest]) returns OkApiResult(GetVIPSolutionsResult(List(createSolutionStub(id = qid, userId = "author")).iterator))

      u.getRandomSolution

      there was one(rand).nextDouble
      there was one(api).getVIPSolutions(any[GetVIPSolutionsRequest])
    }

    "Starting solutions return other solutions" in {
      val qid = "qid"
      val u = User(
        profile = Profile(
          publicProfile = PublicProfile(level = 1)))

      api.config returns createStubConfig
      rand.nextDouble returns 1.0

      api.getAllSolutions(any[GetAllSolutionsRequest]) returns OkApiResult(GetAllSolutionsResult(List(createSolutionStub(id = qid, userId = "author")).iterator))

      u.getRandomSolution

      there was one(rand).nextDouble
      there was one(api).getAllSolutions(any[GetAllSolutionsRequest])
    }

    "Other quests are used if vip quests are unavailable" in {
      val qid = "qid"
      val u = User()

      api.config returns createStubConfig
      rand.nextDouble returns 0.75

      api.getVIPSolutions(any[GetVIPSolutionsRequest]) returns OkApiResult(GetVIPSolutionsResult(List().iterator))
      api.getAllSolutions(any[GetAllSolutionsRequest]) returns OkApiResult(GetAllSolutionsResult(List(createSolutionStub(id = qid, userId = "author")).iterator))

      u.getRandomSolution

      there was one(rand).nextDouble
      there was one(api).getVIPSolutions(any[GetVIPSolutionsRequest])
      there was one(api).getAllSolutions(any[GetAllSolutionsRequest])
    }

    "All solutions are used if vip and Other solutions are unavailable" in {
      val qid = "qid"
      val u = User()

      api.config returns createStubConfig
      rand.nextDouble returns 0.75

      api.getVIPSolutions(any[GetVIPSolutionsRequest]) returns OkApiResult(GetVIPSolutionsResult(List().iterator))
      api.getAllSolutions(any[GetAllSolutionsRequest]) returns OkApiResult(GetAllSolutionsResult(List().iterator)) thenReturns OkApiResult(GetAllSolutionsResult(List(createSolutionStub(id = qid, userId = "author")).iterator))

      u.getRandomSolution

      there was one(rand).nextDouble
      there was one(api).getVIPSolutions(any[GetVIPSolutionsRequest])
      there were two(api).getAllSolutions(any[GetAllSolutionsRequest])
    }
  }
}

