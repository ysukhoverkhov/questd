

package models.store.mongo

import java.util.Date

import models.domain.challenge.{Challenge, ChallengeStatus}
import org.specs2.mutable._
import play.api.test._
import testhelpers.domainstubs._

//noinspection ZeroIndexToHead
class ChallengeDAOSpecs extends Specification
  with MongoDatabaseComponent
  with BaseDAOSpecs {

  "Mongo Challenge DAO" should {
    "Reads by solutions in any ways" in new WithApplication(appWithTestDatabase) {
      db.challenge.clear()

      val mySolutionId = "myId"
      val opponentSolutionId = "opponentId"

      val challenges: List[Challenge] = List(
        Challenge(
          mySolutionId = Some(mySolutionId),
          opponentSolutionId = Some(opponentSolutionId),
          myId = "",
          questId = "",
          opponentId = "",
          status = ChallengeStatus.Accepted),
        Challenge(
          mySolutionId = None,
          opponentSolutionId = Some(opponentSolutionId),
          myId = "",
          questId = "",
          opponentId = "",
          status = ChallengeStatus.Accepted),
        Challenge(
          mySolutionId = Some(mySolutionId),
          opponentSolutionId = None,
          myId = "",
          questId = "",
          opponentId = "",
          status = ChallengeStatus.Accepted)
      )

      challenges.foreach(db.challenge.create)

      db.challenge.findBySolutions((mySolutionId, opponentSolutionId)).toList must beEqualTo(List(challenges.head))
      db.challenge.findBySolutions((opponentSolutionId, mySolutionId)).toList must beEqualTo(List(challenges.head))
    }

    "Reads by participants and quest" in new WithApplication(appWithTestDatabase) {
      db.challenge.clear()

      val questId = "questId"
      val myId = "myId"
      val opponentId = "opponentId"
      val mySolutionId = "myId123"
      val opponentSolutionId = "opponentId123"

      val challenges: List[Challenge] = List(
        Challenge(
          mySolutionId = Some(mySolutionId),
          opponentSolutionId = Some(opponentSolutionId),
          myId = myId,
          questId = questId,
          opponentId = opponentId,
          status = ChallengeStatus.Accepted),
        Challenge(
          mySolutionId = None,
          opponentSolutionId = Some(opponentSolutionId),
          myId = "",
          questId = questId,
          opponentId = opponentId,
          status = ChallengeStatus.Accepted),
        Challenge(
          mySolutionId = Some(mySolutionId),
          opponentSolutionId = None,
          myId = myId,
          questId = questId,
          opponentId = "",
          status = ChallengeStatus.Accepted)
      )

      challenges.foreach(db.challenge.create)

      db.challenge.findByParticipantsAndQuest((myId, opponentId), questId).toList must beEqualTo(List(challenges.head))
      db.challenge.findByParticipantsAndQuest((opponentId, myId), questId).toList must beEqualTo(List(challenges.head))
    }


    "Get all challenges" in new WithApplication(appWithTestDatabase) {
      db.challenge.clear()

      val challenges = List(
        createChallengeStub(
          id = "b1",
          myId = "my_id_1",
          opponentId = "opponent_id_1",
          creationDate = new Date(1),
          status = ChallengeStatus.Accepted),

        createChallengeStub(
          id = "b2",
          myId = "my_id_1",
          opponentId = "opponent_id_2",
          creationDate = new Date(0),
          status = ChallengeStatus.Accepted),

        createChallengeStub(
          id = "b3",
          myId = "my_id_2",
          opponentId = "opponent_id_2",
          creationDate = new Date(3),
          status = ChallengeStatus.AutoCreated),

        createChallengeStub(
          id = "b4",
          myId = "my_id_2",
          opponentId = "opponent_id_1",
          creationDate = new Date(2),
          status = ChallengeStatus.Rejected)
      )

      challenges.foreach(db.challenge.create)

      val all = db.challenge.allWithParams().toList
      all.map(_.id) must beEqualTo(challenges.sortBy(_.creationDate.getTime)(Ordering[Long].reverse).map(_.id))

      val allSkip = db.challenge.allWithParams(skip = 2).toList
      allSkip.map(_.id) must beEqualTo(
        challenges.sortBy(_.creationDate.getTime)(Ordering[Long].reverse).slice(2, 4).map(_.id)
      )

      val status = db.challenge.allWithParams(statuses = List(ChallengeStatus.AutoCreated)).toList
      status.map(_.id).size must beEqualTo(1)
      status.map(_.id) must contain(challenges(2).id)
//      status.map(_.id) must contain(challenges(2).id) and contain(challenges(2).id)

      val myIds = db.challenge.allWithParams(myId = Some("my_id_1")).toList
      myIds.map(_.id) must beEqualTo(
        challenges.slice(0, 2).sortBy(_.creationDate.getTime)(Ordering[Long].reverse).map(_.id)
      )

      val opponentIds = db.challenge.allWithParams(opponentId = Some("opponent_id_2")).toList
      opponentIds.map(_.id) must beEqualTo(
        challenges.slice(1, 3).sortBy(_.creationDate.getTime)(Ordering[Long].reverse).map(_.id)
      )

      val myOpponentIds = db.challenge.allWithParams(
        myId = Some("my_id_1"),
        opponentId = Some("opponent_id_2")).toList
      myOpponentIds.map(_.id) must beEqualTo(
        challenges.slice(1, 2).sortBy(_.creationDate.getTime)(Ordering[Long].reverse).map(_.id)
      )
    }
  }
}

