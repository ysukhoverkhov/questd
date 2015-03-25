package controllers.web.rest.component

import controllers.domain._
import controllers.domain.admin.{AllQuestsRequest, AllSolutionsRequest, AllUsersRequest}
import controllers.domain.app.protocol.ProfileModificationResult
import controllers.domain.app.quest.VoteQuestRequest
import controllers.domain.app.solution.VoteSolutionUpdateRequest
import controllers.domain.app.user._
import controllers.web.rest.component.helpers._
import models.domain._

import scala.annotation.tailrec

private object DebugWSImplTypes {

  type WSShiftDailyResultResult = ShiftDailyResultResult

  case class WSTestResult(r: String)

  case class WSVoteQuestDebugRequest (
    questId: String,
    likesCount: Int,
    cheatingCount: Int,
    pornCount: Int
    )

  case class WSVoteSolutionDebugRequest (
    solutionId: String,
    likesCount: Int,
    cheatingCount: Int,
    pornCount: Int
    )

  case class WSSetFriendshipDebugRequest (
    peerId: String,
    myStatus: String
    )

  case class WSMakeBattleDebugRequest (
    rivalId: Option[String]
    )
}

trait DebugWSImpl extends QuestController with SecurityWSImpl with CommonFunctions { this: WSComponent#WS =>

  import controllers.web.rest.component.DebugWSImplTypes._

  def shiftDailyResult = wrapApiCallReturnBody[WSShiftDailyResultResult] { r =>
    api.resetDailyTasks(ResetDailyTasksRequest(r.user))
    api.populateTimeLineWithRandomThings(PopulateTimeLineWithRandomThingsRequest(r.user))
    api.shiftDailyResult(ShiftDailyResultRequest(r.user))
  }

  def test = wrapApiCallReturnBody[WSTestResult] { r =>

    //      shiftStats(ShiftStatsRequest(user))
//import controllers.domain.app.quest._
//	  calculateProposalThresholds(CalculateProposalThresholdsRequest(10, 3))
//      shiftHistory(ShiftHistoryRequest(user))

    OkApiResult(WSTestResult("lalai"))
  }

  // TODO: move me to lang extention library
  class Rep (count: Int) {
    def times(f: => Unit): Unit = loop(f, count)

    @tailrec
    private def loop (f: => Unit, n: Int): Unit = if (n > 0) { f; loop(f, n - 1) }
  }
  object Rep {
    import scala.language.implicitConversions
    implicit def int2Rep(i: Int): Rep = new Rep(i)
  }

  def voteQuestDebug = wrapJsonApiCallReturnBody[WSTestResult] { (js, r) =>
    import Rep._

    val v = Json.read[WSVoteQuestDebugRequest](js)
    val quest = api.allQuests(AllQuestsRequest()).body.get.quests.filter(_.id == v.questId).next()

    v.likesCount times api.voteQuest(VoteQuestRequest(quest, ContentVote.Cool))
    v.cheatingCount times api.voteQuest(VoteQuestRequest(quest, ContentVote.Cheating))
    v.pornCount times api.voteQuest(VoteQuestRequest(quest, ContentVote.IAPorn))

    OkApiResult(WSTestResult("Done"))
  }

  def voteSolutionDebug = wrapJsonApiCallReturnBody[WSTestResult] { (js, r) =>
    import Rep._

    val v = Json.read[WSVoteSolutionDebugRequest](js)
    val solution = api.allSolutions(AllSolutionsRequest()).body.get.solutions.filter(_.id == v.solutionId).next()

    v.likesCount times api.voteSolutionUpdate(VoteSolutionUpdateRequest(solution, isFriend = false, ContentVote.Cool))
    v.cheatingCount times api.voteSolutionUpdate(VoteSolutionUpdateRequest(solution, isFriend = false, ContentVote.Cheating))
    v.pornCount times api.voteSolutionUpdate(VoteSolutionUpdateRequest(solution, isFriend = false, ContentVote.IAPorn))

    OkApiResult(WSTestResult("Done"))
  }

  //noinspection MutatorLikeMethodIsParameterless
  def setFriendshipDebug = wrapJsonApiCallReturnBody[WSTestResult] { (js, r) =>

    val v = Json.read[WSSetFriendshipDebugRequest](js)
    val peer = api.allUsers(AllUsersRequest()).body.get.users.filter(_.id == v.peerId).next()

    FriendshipStatus.withName(v.myStatus) match {
      case FriendshipStatus.Invites =>
        api.askFriendship(AskFriendshipRequest(r.user, peer.id))
      case FriendshipStatus.Invited =>
        api.askFriendship(AskFriendshipRequest(peer, r.user.id))
      case FriendshipStatus.Accepted =>
        api.askFriendship(AskFriendshipRequest(r.user, peer.id))
        val peer2 = api.allUsers(AllUsersRequest()).body.get.users.filter(_.id == v.peerId).next()
        api.respondFriendship(RespondFriendshipRequest(peer2, r.user.id, accept = true))
    }

    OkApiResult(WSTestResult("Done"))
  }

  def makeBattle = wrapJsonApiCallReturnBody[WSTestResult] { (js, r) =>

    val v = Json.read[WSMakeBattleDebugRequest](js)

    def randomUserExcluding(exclude: Seq[String], vip: Boolean = false) =
      api.allUsers(AllUsersRequest()).body.get.users.filter(u =>
        !exclude.contains(u.id)
          && (u.demo.cultureId != None)
          && (u.profile.publicProfile.bio.gender != Gender.Unknown)
          && (u.profile.publicProfile.vip == vip)).next()



    val peer = {
      v.rivalId.fold[User] {
        randomUserExcluding(List(r.user.id))
      } {
        rivalId => api.allUsers(AllUsersRequest()).body.get.users.filter( u =>
          u.id == rivalId
            && (u.demo.cultureId != None)
            && (u.profile.publicProfile.bio.gender != Gender.Unknown)).next()
      }
    }
    val author = randomUserExcluding(List(r.user.id, peer.id), vip = true)

    {
      // creating quest.
      api.createQuest(CreateQuestRequest(
        user = author,
        quest = QuestInfoContent(
          media = ContentReference(
            contentType = ContentType.Photo,
            storage = "url",
            reference = "http://static-1.questmeapp.com/files/6dd81da7-9992-4552-afb5-82505fdd2cb2.jpg"),
          icon = None,
          description = s"Debug quest for battle between ${r.user.id} and ${peer.id}")))
    } ifOk { rr =>
      assert(rr.allowed == ProfileModificationResult.OK, rr.allowed)

      val questId = api.getUser(UserRequest(userId = Some(author.id))).body.get.user.get.stats.createdQuests.last

      {
        api.addToTimeLine(AddToTimeLineRequest(
          user = r.user,
          reason = TimeLineReason.Created,
          objectType = TimeLineType.Quest,
          objectId = questId,
          actorId = Some(author.id)
        ))
      } ifOk { rr =>

        api.solveQuest(SolveQuestRequest(
          rr.user,
          questId,
          SolutionInfoContent(
            ContentReference(
              contentType = ContentType.Photo,
              storage = "url",
              reference = "http://static-1.questmeapp.com/files/6dd81da7-9992-4552-afb5-82505fdd2cb2.jpg"))))
      } ifOk { rr =>
        assert(rr.allowed == ProfileModificationResult.OK, rr.allowed)

        api.addToTimeLine(AddToTimeLineRequest(
          user = peer,
          reason = TimeLineReason.Created,
          objectType = TimeLineType.Quest,
          objectId = questId,
          actorId = Some(author.id)
        ))

      } ifOk { rr =>

        api.solveQuest(SolveQuestRequest(
          rr.user,
          questId,
          SolutionInfoContent(
            ContentReference(
              contentType = ContentType.Photo,
              storage = "url",
              reference = "http://static-1.questmeapp.com/files/6dd81da7-9992-4552-afb5-82505fdd2cb2.jpg"))))
      }
    } ifOk { rr =>
      assert(rr.allowed == ProfileModificationResult.CoolDown, rr.allowed)

      OkApiResult(WSTestResult("Done"))
    }
  }
}

