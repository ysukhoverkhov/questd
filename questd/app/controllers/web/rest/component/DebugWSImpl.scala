package controllers.web.rest.component

import java.util.Date

import controllers.domain._
import controllers.domain.admin.{AllQuestsRequest, AllSolutionsRequest, AllUsersRequest}
import controllers.domain.app.quest.VoteQuestRequest
import controllers.domain.app.solution.VoteSolutionRequest
import controllers.domain.app.user._
import controllers.services.devicenotifications.DeviceNotifications.IOSDevice
import controllers.services.devicenotifications.{DeviceNotifications, DeviceNotificationsComponent, InactiveDevices}
import controllers.web.helpers._
import models.domain.common.{ContentReference, ContentType, ContentVote}
import models.domain.quest.QuestInfoContent
import models.domain.solution.SolutionInfoContent
import models.domain.user._
import models.domain.user.friends.FriendshipStatus
import models.domain.user.profile.Gender
import models.domain.user.timeline.{TimeLineReason, TimeLineType}
import play.Logger
import play.libs.Akka

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

  type WSResetProfileDebugResult = ResetProfileDebugResult

  type WSResolveAllBattlesResult = ResolveAllBattlesResult

  type WSResetTutorialResult = ResetTutorialResult
}

trait DebugWSImpl extends BaseController with SecurityWSImpl with CommonFunctions { this: WSComponent#WS =>

  import controllers.web.rest.component.DebugWSImplTypes._

  def shiftDailyResult = wrapApiCallReturnBody[WSShiftDailyResultResult] { r =>
    api.db.solution.readManyByIds(r.user.stats.solvedQuests.values.toList).filter(s => s.battleIds.isEmpty).foreach { solution =>
      api.tryCreateBattle(TryCreateBattleRequest(solution))
    }

    api.resetDailyTasks(ResetDailyTasksRequest(r.user))
    api.populateTimeLineWithRandomThings(PopulateTimeLineWithRandomThingsRequest(r.user))
    api.shiftDailyResult(ShiftDailyResultRequest(r.user))
  }

  lazy val c = new DeviceNotificationsComponent(Akka.system)

  def test = wrapApiCallReturnBody[WSDebugResult] { r =>

    c.actor ! DeviceNotifications.PushMessage(
      devices = DeviceNotifications.Devices(Set(IOSDevice("250bad8f be421ebf 716da622 7680bbc3 3cf333e9 ec11a625 487176f6 895bd207"))),
      message = "lalala",
      badge = None,
      sound = None,
      destinations = List(DeviceNotifications.MobileDestination)
    )

  c.actor ! InactiveDevices.GetInactiveDevicesRequest

//    val actorSelectionNotification = Akka.system.actorSelection(s"user/${DeviceNotifications.name}")
//
//    actorSelectionNotification ! DeviceNotifications.PushMessage(
//      devices = DeviceNotifications.Devices(Set(IOSDevice("250bad8f be421ebf 716da622 7680bbc3 3cf333e9 ec11a625 487176f6 895bd207"))),
//      message = "lalala",
//      badge = None,
//      sound = None,
//      destinations = List(DeviceNotifications.MobileDestination)
//    )


//    val users = api.db.user.all
//      .filter(_.demo.cultureId.contains("68349b7a-20ee-4f6e-8406-f468b30be783"))
//      .foldLeft[List[(User, Map[String, (Int, List[String])], Set[String])]](List.empty) { (r, user) =>
//      (
//        user,
//        user.timeLine
//          .take(50)
//          .filter(_.objectType == TimeLineType.Quest)
//          .filter(_.reason == TimeLineReason.Has)
//          .map{ tle =>
//            (api.db.quest.readById(tle.objectId).map(_.cultureId).getOrElse(""), tle.objectId)
//          }
//          .foldLeft(Map.empty[String, (Int, List[String])]) { (r, v) =>
//            val tuple = r.getOrElse(v._1, (0, List.empty))
//            r + (v._1 -> (tuple._1 + 1, v._2 :: tuple._2))
//          },
//        user.following.map(api.db.user.readById(_).map(_.demo.cultureId.getOrElse("")).getOrElse("")).toSet ++
//         user.friends.map(f => api.db.user.readById(f.friendId).map(_.demo.cultureId.getOrElse("")).getOrElse("")).toSet
//      ) :: r
//    }
//      .filter { case (_, cs, _) => cs.size > 1 }
//      .filter { case (_, _, cs) => !cs.contains("d406dc6d-7a84-4ef9-9c64-5e725e8df608")}
//      .sortBy { case (u, _, _) => u.profile.analytics.profileCreationDate }
//
//    Logger.error(s"${users.size}")
//
//    users.foreach {
//      case (user, questCults, friendsCults) =>
//        Logger.error(s"${user.id}, ${user.profile.publicProfile.bio.name}, ${user.profile.analytics.profileCreationDate}, ${questCults.map( m => m._1 -> m._2._1 )/* - "68349b7a-20ee-4f6e-8406-f468b30be783"*/}, $friendsCults")
//    }

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
        Logger.error(log)
      }

      it.next()
    }

    def randomUserExcluding(
      exclude: Seq[String],
      culture: String,
      vip: Boolean = false,
      minLevel: Int = 0) =
      logOrGet(s"Unable to find random user with vip = $vip and excluding $exclude and cultureId $culture and minLevel $minLevel"){
        api.allUsers(AllUsersRequest()).body.get.users.filter(u =>
          !exclude.contains(u.id)
            && u.demo.cultureId.contains(culture)
            && (u.profile.publicProfile.bio.gender != Gender.Unknown)
            && (u.profile.publicProfile.vip == vip)
            && (u.profile.publicProfile.level >= minLevel))
      }

    assert(r.user.demo.cultureId.isDefined, "Culture id of calling user should be defined")
    Logger.debug(s"We found ${r.user.id} / ${r.user.profile.publicProfile.bio.name} / ${r.user.demo.cultureId}")

    val peer = {
      v.rivalId.fold[User] {
        randomUserExcluding(List(r.user.id), r.user.demo.cultureId.get, minLevel = 7)
      } {
        rivalId =>
          logOrGet(s"Unable to find user with id = $rivalId"){
            api.allUsers(AllUsersRequest()).body.get.users.filter( u =>
              u.id == rivalId
                && u.demo.cultureId.isDefined
                && (u.profile.publicProfile.bio.gender != Gender.Unknown))}
      }
    }
    assert(peer.demo.cultureId.isDefined, "Culture id of peer should be defined")
    Logger.debug(s"Peer found ${peer.id} / ${peer.profile.publicProfile.bio.name} / ${peer.demo.cultureId}")

    val author = randomUserExcluding(List(r.user.id, peer.id), r.user.demo.cultureId.get, vip = true, minLevel = 10)
    Logger.debug(s"Author found ${author.id} / ${author.profile.publicProfile.bio.name} / ${author.demo.cultureId}")

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
      assert(rr.allowed == CreateQuestCode.OK, rr.allowed)

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
              reference = "http://static-1.questmeapp.com/files/6dd81da7-9992-4552-afb5-82505fdd2cb2.jpg"),
            description = ""))) map { r =>
                val s = api.db.solution.readById(r.modifiedSolutions.head.id).get
                val s2 = s.copy(info = s.info.copy(creationDate = new Date(0)))
                api.db.solution.update(s2)
                OkApiResult(r)
              }
      } map { rr =>
        Logger.debug(s"Quest solved with result ${rr.allowed}")
        assert(rr.allowed == SolveQuestCode.OK, rr.allowed)

        api.addToTimeLine(AddToTimeLineRequest(
          user = peer,
          reason = TimeLineReason.Created,
          objectType = TimeLineType.Quest,
          objectId = questId,
          actorId = Some(author.id)
        ))

      } map { rr =>

        Logger.debug(s"Quest added to timeline of peer")

        api.solveQuest(SolveQuestRequest(
          rr.user,
          questId,
          SolutionInfoContent(
            ContentReference(
              contentType = ContentType.Photo,
              storage = "url",
              reference = "http://static-1.questmeapp.com/files/6dd81da7-9992-4552-afb5-82505fdd2cb2.jpg"),
            description = "asd"))) map { r =>
                val s = api.db.solution.readById(r.modifiedSolutions.head.id).get
                val s2 = s.copy(info = s.info.copy(creationDate = new Date(0)))
                api.db.solution.update(s2)

                api.tryCreateBattle(TryCreateBattleRequest(s2))

                OkApiResult(r)
              }
      }
    } map { rr =>
      Logger.debug(s"Quest solved by peer with result ${rr.allowed}")
      assert(rr.allowed == SolveQuestCode.OK, rr.allowed)

      OkApiResult(WSDebugResult("Done"))
    }
  }

  def resetTutorial = wrapJsonApiCallReturnBody[WSResetTutorialResult] { (js, r) =>
    api.resetTutorial(ResetTutorialRequest(r.user))
  }

  //noinspection MutatorLikeMethodIsParameterless
  def setLevel = wrapJsonApiCallReturnBody[WSDebugResult] { (js, r) =>
    val v = Json.read[WSSetLevelRequest](js)

    api.setLevelDebug(SetLevelDebugRequest(r.user, v.level))

    OkApiResult(WSDebugResult("Done"))
  }

  /**
   * Resets our profile to no tutorial, level one and no assets.
   *
   * @return
   */
  def resetProfile = wrapApiCallReturnBody[WSResetProfileDebugResult] { r =>
    api.resetProfileDebug(ResetProfileDebugRequest(r.user))
  }

  def resolveAllBattles = wrapApiCallReturnBody[WSResolveAllBattlesResult] { r =>
    api.resolveAllBattles(ResolveAllBattlesRequest(r.user))
  }

  def generateErrorLog = wrapJsonApiCallReturnBody[WSDebugResult] { (js, r) =>
    Logger.error(s"Error log generated with request by ${r.user.id}")

    OkApiResult(WSDebugResult("Error log generated"))
  }
}

