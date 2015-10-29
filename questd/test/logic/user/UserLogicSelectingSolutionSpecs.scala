package logic.user

import controllers.domain.OkApiResult
import controllers.domain.app.solution._
import logic.BaseLogicSpecs
import models.domain.user.User
import testhelpers.domainstubs._

class UserLogicSelectingSolutionSpecs extends BaseLogicSpecs {

  private def createFriend(newid: String) = {
    User(id = newid)
  }

  "User Logic solution selector" should {

    "Return solution from friends if dice rolls so" in context {
      rand.nextDouble returns 0.13

      val qid = "qid"

      api.getSolutionsForOwnQuests(any[GetSolutionsForOwnQuestsRequest]) returns OkApiResult(GetSolutionsForOwnQuestsResult(List.empty.iterator))
      api.getFriendsSolutions(any[GetFriendsSolutionsRequest]) returns OkApiResult(GetFriendsSolutionsResult(List(createSolutionStub(id = qid, authorId = "author")).iterator))

      val u = createUserStub()
      val q = u.getRandomSolution

      there was one(rand).nextDouble
      there was one(api).getFriendsSolutions(any[GetFriendsSolutionsRequest])

      q must beSome.which(q => q.id == qid)
    }

    "Return solution from following if dice rolls so" in context {
      rand.nextDouble returns 0.38

      val qid = "qid"

      api.getSolutionsForOwnQuests(any[GetSolutionsForOwnQuestsRequest]) returns OkApiResult(GetSolutionsForOwnQuestsResult(List.empty.iterator))
      api.getFollowingSolutions(any[GetFollowingSolutionsRequest]) returns OkApiResult(GetFollowingSolutionsResult(List(createSolutionStub(id = qid, authorId = "author")).iterator))

      val u = createUserStub()
      val q = u.getRandomSolution

      there was one(rand).nextDouble
      there was one(api).getFollowingSolutions(any[GetFollowingSolutionsRequest])

      q must beSome.which(q => q.id == qid)
    }

    "Return liked quest if dice rolls so" in context {
      rand.nextDouble returns 0.58

      val qid = "qid"

      api.getSolutionsForOwnQuests(any[GetSolutionsForOwnQuestsRequest]) returns OkApiResult(GetSolutionsForOwnQuestsResult(List.empty.iterator))
      api.getSolutionsForLikedQuests(any[GetSolutionsForLikedQuestsRequest]) returns OkApiResult(GetSolutionsForLikedQuestsResult(List(createSolutionStub(id = qid, authorId = "author")).iterator))

      val u = createUserStub()
      val q = u.getRandomSolution

      there was one(rand).nextDouble
      there was one(api).getSolutionsForLikedQuests(any[GetSolutionsForLikedQuestsRequest])

      q must beSome.which(q => q.id == qid)
    }

    "Return VIP solutions if dice rolls so" in context {
      rand.nextDouble returns 0.75

      val qid = "qid"

      api.getSolutionsForOwnQuests(any[GetSolutionsForOwnQuestsRequest]) returns OkApiResult(GetSolutionsForOwnQuestsResult(List.empty.iterator))
      api.getVIPSolutions(any[GetVIPSolutionsRequest]) returns OkApiResult(GetVIPSolutionsResult(List(createSolutionStub(id = qid, authorId = "author")).iterator))

      val u = createUserStub()
      val q = u.getRandomSolution

      there was one(rand).nextDouble
      there was one(api).getVIPSolutions(any[GetVIPSolutionsRequest])

      q must beSome.which(q => q.id == qid)
    }

    // TAGS: uncomment it for tags.
//    "Return VIP solutions with favorite theme ids if dice rolls so" in context {
//      val qid = "qid"
//      val u = User(
//        profile = Profile(
//          publicProfile = PublicProfile(level = 10)),
//        history = UserHistory(
//          themesOfSelectedQuests = List("1", "2", "3", "4")))
//
//      api.config returns createStubConfig
//      rand.nextDouble returns 0.75
//      rand.nextInt(4) returns 0 thenReturns 1 thenReturns 2
//
//      api.getSolutionsForOwnQuests(any[GetSolutionsForOwnQuestsRequest]) returns OkApiResult(GetSolutionsForOwnQuestsResult(List.empty.iterator))
//      api.getVIPSolutions(GetVIPSolutionsRequest(u, QuestSolutionStatus.OnVoting, Some((-20, 12)), List("1", "2", "3"))) returns OkApiResult(GetVIPSolutionsResult(List(createSolutionStub(id = qid, userId = "author")).iterator))
//
//      val q = u.getRandomSolution
//
//      there was one(rand).nextDouble
//      there were three(rand).nextInt(4)
//      there was one(api).getVIPSolutions(GetVIPSolutionsRequest(u, QuestSolutionStatus.OnVoting, Some((-20, 12)), List("1", "2", "3")))
//
//      q must beSome.which(q => q.id == qid)
//    }

    // TAGS: uncomment it for tags.
//    "Return All solutions with favorite theme ids if dice rolls so" in context {
//      val qid = "qid"
//      val u = User(
//        history = UserHistory(
//          themesOfSelectedQuests = List("1", "2", "3", "4")))
//
//      api.config returns createStubConfig
//      rand.nextDouble returns 0.95
//      rand.nextInt(4) returns 1
//
//      api.getSolutionsForOwnQuests(any[GetSolutionsForOwnQuestsRequest]) returns OkApiResult(GetSolutionsForOwnQuestsResult(List.empty.iterator))
//      api.getAllSolutions(GetAllSolutionsRequest(u, QuestSolutionStatus.OnVoting, Some((-12, 20)), List("2"))) returns OkApiResult(GetAllSolutionsResult(List(createSolutionStub(id = qid, userId = "author")).iterator))
//
//      val q = u.getRandomSolution
//
//      there was one(rand).nextDouble
//      there was one(rand).nextInt(4)
//      there was one(api).getAllSolutions(GetAllSolutionsRequest(u, QuestSolutionStatus.OnVoting, Some((-12, 20)), List("2")))
//
//      q must beSome.which(q => q.id == qid)
//    }

    "Starting solutions return vip solutions" in context {
      val qid = "qid"
      val u = createUserStub(level = 1)

      rand.nextDouble returns 0.0

      api.getSolutionsForOwnQuests(any[GetSolutionsForOwnQuestsRequest]) returns OkApiResult(GetSolutionsForOwnQuestsResult(List.empty.iterator))
      api.getVIPSolutions(any[GetVIPSolutionsRequest]) returns OkApiResult(GetVIPSolutionsResult(List(createSolutionStub(id = qid, authorId = "author")).iterator))

      u.getRandomSolution

      there was one(rand).nextDouble
      there was one(api).getVIPSolutions(any[GetVIPSolutionsRequest])
    }

    "Starting solutions return other solutions" in context {
      val qid = "qid"
      val u = createUserStub(level = 1)

      rand.nextDouble returns 1.0

      api.getSolutionsForOwnQuests(any[GetSolutionsForOwnQuestsRequest]) returns OkApiResult(GetSolutionsForOwnQuestsResult(List.empty.iterator))
      api.getAllSolutions(any[GetAllSolutionsRequest]) returns OkApiResult(GetAllSolutionsResult(List(createSolutionStub(id = qid, authorId = "author")).iterator))

      u.getRandomSolution

      there was one(rand).nextDouble
      there was one(api).getAllSolutions(any[GetAllSolutionsRequest])
    }

    "Other quests are used if vip quests are unavailable" in context {
      val qid = "qid"
      val u = createUserStub()

      rand.nextDouble returns 0.75

      api.getSolutionsForOwnQuests(any[GetSolutionsForOwnQuestsRequest]) returns OkApiResult(GetSolutionsForOwnQuestsResult(List.empty.iterator))
      api.getVIPSolutions(any[GetVIPSolutionsRequest]) returns OkApiResult(GetVIPSolutionsResult(List.empty.iterator))
      api.getAllSolutions(any[GetAllSolutionsRequest]) returns OkApiResult(GetAllSolutionsResult(List(createSolutionStub(id = qid, authorId = "author")).iterator))

      u.getRandomSolution

      there was one(rand).nextDouble
      there was one(api).getVIPSolutions(any[GetVIPSolutionsRequest])
      there was one(api).getAllSolutions(any[GetAllSolutionsRequest])
    }

    "All solutions are used if vip and Other solutions are unavailable" in context {
      val qid = "qid"
      val u = createUserStub()
      val s = createSolutionStub(id = qid, authorId = "author")

      rand.nextDouble returns 0.75

      api.getSolutionsForOwnQuests(any[GetSolutionsForOwnQuestsRequest]) returns OkApiResult(GetSolutionsForOwnQuestsResult(List.empty.iterator))
      api.getVIPSolutions(any[GetVIPSolutionsRequest]) returns OkApiResult(GetVIPSolutionsResult(List.empty.iterator))
      api.getAllSolutions(any[GetAllSolutionsRequest]) returns OkApiResult(GetAllSolutionsResult(List.empty.iterator)) thenReturns OkApiResult(GetAllSolutionsResult(List(s).iterator))

      val rv = u.getRandomSolution

      rv must beSome

      there was one(rand).nextDouble
      there was one(api).getSolutionsForOwnQuests(any[GetSolutionsForOwnQuestsRequest])
      there was one(api).getVIPSolutions(any[GetVIPSolutionsRequest])
      there were two(api).getAllSolutions(any[GetAllSolutionsRequest])
    }

    "Solutions for own quests are returned" in context {
      val qid = "qid"
      val u = createUserStub()

      api.getSolutionsForOwnQuests(any[GetSolutionsForOwnQuestsRequest]) returns OkApiResult(GetSolutionsForOwnQuestsResult(List(createSolutionStub(id = qid)).iterator))

      u.getRandomSolution

      there was one(api).getSolutionsForOwnQuests(any[GetSolutionsForOwnQuestsRequest])
    }

  }
}

