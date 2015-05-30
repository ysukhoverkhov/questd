package controllers.web.rest.component

import controllers.domain._
import controllers.domain.admin.{AllQuestsRequest, AllSolutionsRequest, AllUsersRequest}
import controllers.domain.app.protocol.ProfileModificationResult
import controllers.domain.app.quest.VoteQuestRequest
import controllers.domain.app.solution.VoteSolutionRequest
import controllers.domain.app.user._
import controllers.web.helpers._
import models.domain.common.{ContentVote, ContentType, ContentReference}
import models.domain.quest.QuestInfoContent
import models.domain.solution.SolutionInfoContent
import models.domain.user._
import play.Logger

private object DebugWSImplTypes {

  type WSShiftDailyResultResult = ShiftDailyResultResult

  case class WSDebugResult(r: String)

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

  case class WSSetLevelRequest (
    level: Int
    )
}

trait DebugWSImpl extends QuestController with SecurityWSImpl with CommonFunctions { this: WSComponent#WS =>

  import controllers.web.rest.component.DebugWSImplTypes._

  def shiftDailyResult = wrapApiCallReturnBody[WSShiftDailyResultResult] { r =>
    api.resetDailyTasks(ResetDailyTasksRequest(r.user))
    api.populateTimeLineWithRandomThings(PopulateTimeLineWithRandomThingsRequest(r.user))
    api.shiftDailyResult(ShiftDailyResultRequest(r.user))
  }

  def test = wrapApiCallReturnBody[WSDebugResult] { r =>
    api.populateTimeLineWithRandomThings(PopulateTimeLineWithRandomThingsRequest(r.user))

    OkApiResult(WSDebugResult("lalai"))
  }

  def voteQuestDebug = wrapJsonApiCallReturnBody[WSDebugResult] { (js, r) =>
    import com.vita.utils._

    val v = Json.read[WSVoteQuestDebugRequest](js)
    val quest = api.allQuests(AllQuestsRequest()).body.get.quests.filter(_.id == v.questId).next()

    v.likesCount times api.voteQuest(VoteQuestRequest(quest, ContentVote.Cool))
    v.cheatingCount times api.voteQuest(VoteQuestRequest(quest, ContentVote.Cheating))
    v.pornCount times api.voteQuest(VoteQuestRequest(quest, ContentVote.IAPorn))

    OkApiResult(WSDebugResult("Done"))
  }

  def voteSolutionDebug = wrapJsonApiCallReturnBody[WSDebugResult] { (js, r) =>
    import com.vita.utils._

    val v = Json.read[WSVoteSolutionDebugRequest](js)
    val solution = api.allSolutions(AllSolutionsRequest()).body.get.solutions.filter(_.id == v.solutionId).next()

    v.likesCount times api.voteSolution(VoteSolutionRequest(solution, isFriend = false, ContentVote.Cool))
    v.cheatingCount times api.voteSolution(VoteSolutionRequest(solution, isFriend = false, ContentVote.Cheating))
    v.pornCount times api.voteSolution(VoteSolutionRequest(solution, isFriend = false, ContentVote.IAPorn))

    OkApiResult(WSDebugResult("Done"))
  }

  //noinspection MutatorLikeMethodIsParameterless
  def setFriendshipDebug = wrapJsonApiCallReturnBody[WSDebugResult] { (js, r) =>

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

    OkApiResult(WSDebugResult("Done"))
  }

  def makeBattle = wrapJsonApiCallReturnBody[WSDebugResult] { (js, r) =>

    val v = Json.read[WSMakeBattleDebugRequest](js)

    def logOrGet[T](log: String)(it: Iterator[T]) = {
      if (!it.hasNext) {
        Logger.debug(log)
      }

      it.next()
    }

    def randomUserExcluding(exclude: Seq[String], vip: Boolean = false) =
      logOrGet(s"Unable to find random user with vip = $vip and excluding $exclude"){
        api.allUsers(AllUsersRequest()).body.get.users.filter(u =>
          !exclude.contains(u.id)
            && u.demo.cultureId.isDefined
            && (u.profile.publicProfile.bio.gender != Gender.Unknown)
            && (u.profile.publicProfile.vip == vip))
      }

    val peer = {
      v.rivalId.fold[User] {
        randomUserExcluding(List(r.user.id))
      } {
        rivalId =>
          logOrGet(s"Unable to find user with id = $rivalId"){
            api.allUsers(AllUsersRequest()).body.get.users.filter( u =>
              u.id == rivalId
                && u.demo.cultureId.isDefined
                && (u.profile.publicProfile.bio.gender != Gender.Unknown))}
      }
    }
    Logger.debug(s"Peer found ${peer.id} / ${peer.profile.publicProfile.bio.name}")

    val author = randomUserExcluding(List(r.user.id, peer.id), vip = true)
    Logger.debug(s"Author found ${author.id} / ${author.profile.publicProfile.bio.name}")

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
    } map { rr =>
      assert(rr.allowed == ProfileModificationResult.OK, rr.allowed)

      val questId = api.getUser(GetUserRequest(userId = Some(author.id))).body.get.user.get.stats.createdQuests.last

      Logger.debug(s"Quest for debug battle created id = $questId")

      {
        api.addToTimeLine(AddToTimeLineRequest(
          user = r.user,
          reason = TimeLineReason.Created,
          objectType = TimeLineType.Quest,
          objectId = questId,
          actorId = Some(author.id)
        ))
      } map { rr =>

        Logger.debug(s"Quest added to timeline")

        api.solveQuest(SolveQuestRequest(
          rr.user,
          questId,
          SolutionInfoContent(
            ContentReference(
              contentType = ContentType.Photo,
              storage = "url",
              reference = "http://static-1.questmeapp.com/files/6dd81da7-9992-4552-afb5-82505fdd2cb2.jpg"))))
      } map { rr =>
        assert(rr.allowed == ProfileModificationResult.OK, rr.allowed)

        Logger.debug(s"Quest solved")

        api.addToTimeLine(AddToTimeLineRequest(
          user = peer,
          reason = TimeLineReason.Created,
          objectType = TimeLineType.Quest,
          objectId = questId,
          actorId = Some(author.id)
        ))

      } map { rr =>

        Logger.debug(s"Quest added to timeline")

        api.solveQuest(SolveQuestRequest(
          rr.user,
          questId,
          SolutionInfoContent(
            ContentReference(
              contentType = ContentType.Photo,
              storage = "url",
              reference = "http://static-1.questmeapp.com/files/6dd81da7-9992-4552-afb5-82505fdd2cb2.jpg"))))
      }
    } map { rr =>
      assert(rr.allowed == ProfileModificationResult.OK, rr.allowed)

      Logger.debug(s"Quest solved")

      OkApiResult(WSDebugResult("Done"))
    }
  }

  def resetTutorial = wrapJsonApiCallReturnBody[ResetTutorialResult] { (js, r) =>
    api.resetTutorial(ResetTutorialRequest(r.user))
  }

  //noinspection MutatorLikeMethodIsParameterless
  def setLevel = wrapJsonApiCallReturnBody[WSDebugResult] { (js, r) =>
    val v = Json.read[WSSetLevelRequest](js)

    api.setLevelDebug(SetLevelDebugRequest(r.user, v.level))

    OkApiResult(WSDebugResult("Done"))
  }

  def generateErrorLog = wrapJsonApiCallReturnBody[WSDebugResult] { (js, r) =>
    Logger.error(s"Error log generated with request by ${r.user.id}")

    OkApiResult(WSDebugResult("Error log generated"))
  }
}

