package controllers.domain.app.user

import components._
import controllers.domain.{DomainAPIComponent, _}
import controllers.domain.app.protocol.ProfileModificationResult._
import controllers.domain.helpers._
import models.domain._
import models.domain.view._
import controllers.domain.helpers.PagerHelper._
import play.Logger

case class GetQuestsRequest(user: User, questIds: List[String])
case class GetQuestsResult(
  allowed: ProfileModificationResult,
  quests: List[QuestInfoWithID] = List.empty)

case class GetSolutionsRequest(user: User, solutionIds: List[String])
case class GetSolutionsResult(
  allowed: ProfileModificationResult,
  solutions: List[SolutionInfoWithID] = List.empty,
  ratings: List[SolutionRating] = List.empty,
  quests: List[QuestInfoWithID] = List.empty,
  questAuthors: List[PublicProfileWithID] = List.empty)

case class GetBattlesRequest(user: User, battleIds: List[String])
case class GetBattlesResult(
  allowed: ProfileModificationResult,
  battles: List[BattleInfoWithID] = List.empty)

case class GetPublicProfilesRequest(
  user: User,
  userIds: List[String])
case class GetPublicProfilesResult(
  allowed: ProfileModificationResult,
  publicProfiles: List[PublicProfileWithID])

case class GetOwnSolutionsRequest(
  user: User,
  status: List[SolutionStatus.Value],
  pageNumber: Int,
  pageSize: Int)
case class GetOwnSolutionsResult(
  allowed: ProfileModificationResult,
  solutions: List[SolutionListInfo],
  pageSize: Int,
  hasMore: Boolean)

case class GetOwnQuestsRequest(
  user: User,
  status: List[QuestStatus.Value],
  pageNumber: Int,
  pageSize: Int)
case class GetOwnQuestsResult(
  allowed: ProfileModificationResult,
  quests: List[QuestInfoWithID],
  pageSize: Int,
  hasMore: Boolean)

case class GetSolutionsForQuestRequest(
  user: User,
  questId: String,
  status: List[SolutionStatus.Value],
  pageNumber: Int,
  pageSize: Int)
case class GetSolutionsForQuestResult(
  allowed: ProfileModificationResult,
  solutions: List[SolutionListInfo],
  pageSize: Int,
  hasMore: Boolean)

case class GetSolutionsForUserRequest(
  user: User,
  userId: String,
  status: List[SolutionStatus.Value],
  pageNumber: Int,
  pageSize: Int)
case class GetSolutionsForUserResult(
  allowed: ProfileModificationResult,
  solutions: List[SolutionListInfo],
  pageSize: Int,
  hasMore: Boolean)

case class GetQuestsForUserRequest(
  user: User,
  userId: String,
  status: List[QuestStatus.Value],
  pageNumber: Int,
  pageSize: Int)
case class GetQuestsForUserResult(
  allowed: ProfileModificationResult,
  quests: List[QuestInfoWithID],
  pageSize: Int,
  hasMore: Boolean)

private[domain] trait ContentAPI { this: DomainAPIComponent#DomainAPI with DBAccessor =>

  /**
   * Get quest by its id
   */
  def getQuests(request: GetQuestsRequest): ApiResult[GetQuestsResult] = handleDbException {
    import request._
    val maxPageSize = adjustedPageSize(questIds.length)

    OkApiResult(GetQuestsResult(
      OK,
      db.quest.readManyByIds(questIds.take(maxPageSize)).map(q => QuestInfoWithID(q.id, q.info)).toList))
  }

  /**
   * Get solution by its id
   */
  def getSolutions(request: GetSolutionsRequest): ApiResult[GetSolutionsResult] = handleDbException {
    import request._
    val maxPageSize = adjustedPageSize(solutionIds.length)

    val result = db.solution.readManyByIds(solutionIds.take(maxPageSize)).foldLeft[List[(SolutionInfoWithID, SolutionRating, QuestInfoWithID, PublicProfileWithID)]] (List.empty) {
      case (r, s) =>
        val quest = db.quest.readById(s.info.questId)
        quest.map(q => QuestInfoWithID(q.id, q.info)).fold {
          Logger.error(s"Unable to find quest ${s.info.questId} for solution ${s.id}")
          r
        } {
          case questInfo =>
            quest.flatMap(q => db.user.readById(q.info.authorId).map(u => PublicProfileWithID(u.id, u.profile.publicProfile))).fold {
              Logger.error(s"Unable to find user ${quest.get.info.authorId} for quest ${quest.get.id}")
              r
            } {
              case questAuthor =>
                (
                  SolutionInfoWithID(s.id, s.info),
                  s.rating,
                  questInfo,
                  questAuthor
                  ) :: r
            }
        }
    }

    OkApiResult(GetSolutionsResult(
      allowed = OK,
      solutions = result.map(_._1).toList,
      ratings = result.map(_._2).toList,
      quests = result.map(_._3).toList,
      questAuthors = result.map(_._4).toList))
  }

  /**
   * Get battle by its id.
   * @param request The request.
   * @return
   */
  def getBattles(request: GetBattlesRequest): ApiResult[GetBattlesResult] = handleDbException {
    import request._
    val maxPageSize = adjustedPageSize(battleIds.length)

    OkApiResult(GetBattlesResult(
      OK,
      db.battle.readManyByIds(battleIds.take(maxPageSize)).map(b => BattleInfoWithID(b.id, b.info)).toList))
  }

  /**
   * Get public profile
   */
  def getPublicProfiles(request: GetPublicProfilesRequest): ApiResult[GetPublicProfilesResult] = handleDbException {
    val maxPageSize = adjustedPageSize(request.userIds.length)

    OkApiResult(GetPublicProfilesResult(
      allowed = OK,
      publicProfiles = db.user.readManyByIds(request.userIds.take(maxPageSize)).toList.map(u => PublicProfileWithID(u.id, u.profile.publicProfile))))
  }

  /**
   * Get own solutions.
   */
  def getOwnSolutions(request: GetOwnSolutionsRequest): ApiResult[GetOwnSolutionsResult] = handleDbException {
    val pageSize = adjustedPageSize(request.pageSize)
    val pageNumber = adjustedPageNumber(request.pageNumber)

    val solutionsForUser = db.solution.allWithParams(
      status = request.status,
      authorIds = List(request.user.id),
      skip = pageNumber * pageSize)

    val solutions = solutionsForUser.take(pageSize).toList.map(s => {
      SolutionListInfo(
        solution = SolutionInfoWithID(s.id, s.info),
        quest = db.quest.readById(s.info.questId).map(qu => QuestInfoWithID(qu.id, qu.info)),
        author = None)
    })

    OkApiResult(GetOwnSolutionsResult(
      allowed = OK,
      solutions = solutions,
      pageSize,
      solutionsForUser.hasNext))
  }

  /**
   * Get own quests.
   */
  def getOwnQuests(request: GetOwnQuestsRequest): ApiResult[GetOwnQuestsResult] = handleDbException {
    val pageSize = adjustedPageSize(request.pageSize)
    val pageNumber = adjustedPageNumber(request.pageNumber)

    val questsForUser = db.quest.allWithParams(
      status = request.status,
      authorIds = List(request.user.id),
      skip = pageNumber * pageSize)

    OkApiResult(GetOwnQuestsResult(
      allowed = OK,
      quests = questsForUser.take(pageSize).toList.map(q => QuestInfoWithID(q.id, q.info)),
      pageSize,
      questsForUser.hasNext))
  }

  /**
   * Get solutions for a quest.
   */
  def getSolutionsForQuest(request: GetSolutionsForQuestRequest): ApiResult[GetSolutionsForQuestResult] = handleDbException {
    val pageSize = adjustedPageSize(request.pageSize)
    val pageNumber = adjustedPageNumber(request.pageNumber)

    val solutionsForQuest = db.solution.allWithParams(
      status = request.status.filter(Set(SolutionStatus.Won, SolutionStatus.Lost).contains),
      questIds = List(request.questId),
      skip = pageNumber * pageSize)

    val solutions = solutionsForQuest.take(pageSize).toList.map(s => {
      SolutionListInfo(
        solution = SolutionInfoWithID(s.id, s.info),
        quest = None,
        author = db.user.readById(s.info.authorId).map(us => PublicProfileWithID(us.id, us.profile.publicProfile)))
    })

    OkApiResult(GetSolutionsForQuestResult(
      allowed = OK,
      solutions = solutions,
      pageSize,
      solutionsForQuest.hasNext))
  }

  /**
   * Get all solutions for a user.
   */
  def getSolutionsForUser(request: GetSolutionsForUserRequest): ApiResult[GetSolutionsForUserResult] = handleDbException {
    val pageSize = adjustedPageSize(request.pageSize)
    val pageNumber = adjustedPageNumber(request.pageNumber)

    val solutionsForUser = db.solution.allWithParams(
      status = request.status.filter(Set(SolutionStatus.Won, SolutionStatus.Lost).contains),
      authorIds = List(request.userId),
      skip = pageNumber * pageSize)

    val solutions = solutionsForUser.take(pageSize).toList.map(s => {
      SolutionListInfo(
        solution = SolutionInfoWithID(s.id, s.info),
        quest = db.quest.readById(s.info.questId).map(qu => QuestInfoWithID(qu.id, qu.info)),
        author = None)
    })

    OkApiResult(GetSolutionsForUserResult(
      allowed = OK,
      solutions = solutions,
      pageSize,
      solutionsForUser.hasNext))
  }

  /**
   * Get all quests for a user.
   */
  def getQuestsForUser(request: GetQuestsForUserRequest): ApiResult[GetQuestsForUserResult] = handleDbException {
    val pageSize = adjustedPageSize(request.pageSize)
    val pageNumber = adjustedPageNumber(request.pageNumber)

    val questsForUser = db.quest.allWithParams(
      status = request.status.filter(Set(QuestStatus.InRotation).contains),
      authorIds = List(request.userId),
      skip = pageNumber * pageSize)

    OkApiResult(GetQuestsForUserResult(
      allowed = OK,
      quests = questsForUser.take(pageSize).toList.map(q => QuestInfoWithID(q.id, q.info)),
      pageSize,
      questsForUser.hasNext))
  }

}

