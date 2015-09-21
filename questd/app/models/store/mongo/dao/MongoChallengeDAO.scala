package models.store.mongo.dao

import com.mongodb.casbah.commons.MongoDBObject
import models.domain.challenge.{ChallengeStatus, Challenge}
import models.store.dao._
import models.store.mongo.helpers._

/**
 * DOA for Comments
 */
private[mongo] class MongoChallengeDAO
  extends BaseMongoDAO[Challenge](collectionName = "challenges")
  with ChallengeDAO {

  /**
   * @inheritdoc
   */
  def findBySolutions(solutionIds: (String, String)): Iterator[Challenge] = {
    val queryBuilder = MongoDBObject.newBuilder

    queryBuilder += ("$or" -> List(
      MongoDBObject("$and" -> List(
        MongoDBObject("mySolutionId" -> solutionIds._1 ),
        MongoDBObject("opponentSolutionId" -> solutionIds._2)
      )),
      MongoDBObject("$and" -> List(
        MongoDBObject("mySolutionId" -> solutionIds._2 ),
        MongoDBObject("opponentSolutionId" -> solutionIds._1)
      ))
    ))

    findByExample(
      example = queryBuilder.result(),
      skip = 0)
  }

  /**
   * @inheritdoc
   */
  def findByParticipantsAndQuest(participantIds: (String, String), questId: String): Iterator[Challenge] = {
    val queryBuilder = MongoDBObject.newBuilder

    queryBuilder += ("$or" -> List(
      MongoDBObject("$and" -> List(
        MongoDBObject("myId" -> participantIds._1 ),
        MongoDBObject("opponentId" -> participantIds._2)
      )),
      MongoDBObject("$and" -> List(
        MongoDBObject("myId" -> participantIds._2 ),
        MongoDBObject("opponentId" -> participantIds._1)
      ))
    ))

    queryBuilder += ("questId" -> questId)

    findByExample(
      example = queryBuilder.result(),
      skip = 0)
  }

  /**
   * @inheritdoc
   */
  def allWithParams(
    myId: Option[String] = None,
    opponentId: Option[String] = None,
    statuses: List[ChallengeStatus.Value] = List.empty,
    skip: Int = 0): Iterator[Challenge] = {

    val queryBuilder = MongoDBObject.newBuilder

    if (myId.nonEmpty) {
      queryBuilder += ("myId" -> myId)
    }

    if (opponentId.nonEmpty) {
      queryBuilder += ("opponentId" -> opponentId)
    }

    if (statuses.nonEmpty) {
      queryBuilder += ("status" -> MongoDBObject("$in" -> statuses.map(_.toString)))
    }

    findByExample(
      example = queryBuilder.result(),
      sort = MongoDBObject(
        "creationDate" -> -1),
      skip = skip)

  }


}

