

package models.store.mongo

import java.util.Date

import models.domain.challenge.{Challenge, ChallengeStatus}
import play.api.test._
import testhelpers.domainstubs._

//noinspection ZeroIndexToHead
class ChallengeDAOSpecs extends BaseDAOSpecs {

  "Mongo Challenge DAO" should {
    "Reads by solutions in any ways" in new WithApplication(appWithTestDatabase) {
      db.challenge.clear()

      val mySolutionId = "myId"
      val opponentSolutionId = "opponentId"

      val challenges: List[Challenge] = List(
        createChallengeStub(
          initiatorSolutionId = Some(mySolutionId),
          opponentSolutionId = Some(opponentSolutionId)),
        createChallengeStub(
          initiatorSolutionId = None,
          opponentSolutionId = Some(opponentSolutionId)),
        createChallengeStub(
          initiatorSolutionId = Some(mySolutionId),
          opponentSolutionId = None)
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
        createChallengeStub(
          initiatorSolutionId = Some(mySolutionId),
          opponentSolutionId = Some(opponentSolutionId),
          questId = questId,
          opponentId = opponentId,
          initiatorId = myId,
          status = ChallengeStatus.Accepted),
        createChallengeStub(
          initiatorSolutionId = None,
          opponentSolutionId = Some(opponentSolutionId),
          questId = questId,
          status = ChallengeStatus.Accepted),
        createChallengeStub(
          initiatorSolutionId = Some(mySolutionId),
          opponentSolutionId = None,
          questId = questId,
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
          initiatorId = "my_id_1",
          opponentId = "opponent_id_1",
          creationDate = new Date(1),
          status = ChallengeStatus.Accepted),

        createChallengeStub(
          id = "b2",
          initiatorId = "my_id_1",
          opponentId = "opponent_id_2",
          creationDate = new Date(0),
          status = ChallengeStatus.Accepted),

        createChallengeStub(
          id = "b3",
          initiatorId = "my_id_2",
          opponentId = "opponent_id_2",
          creationDate = new Date(3),
          status = ChallengeStatus.AutoCreated),

        createChallengeStub(
          id = "b4",
          initiatorId = "my_id_2",
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

      val myIds = db.challenge.allWithParams(initiatorId = Some("my_id_1")).toList
      myIds.map(_.id) must beEqualTo(
        challenges.slice(0, 2).sortBy(_.creationDate.getTime)(Ordering[Long].reverse).map(_.id)
      )

      val opponentIds = db.challenge.allWithParams(opponentId = Some("opponent_id_2")).toList
      opponentIds.map(_.id) must beEqualTo(
        challenges.slice(1, 3).sortBy(_.creationDate.getTime)(Ordering[Long].reverse).map(_.id)
      )

      val myOpponentIds = db.challenge.allWithParams(
        initiatorId = Some("my_id_1"),
        opponentId = Some("opponent_id_2")).toList
      myOpponentIds.map(_.id) must beEqualTo(
        challenges.slice(1, 2).sortBy(_.creationDate.getTime)(Ordering[Long].reverse).map(_.id)
      )
    }

    "updateChallenge" in new WithApplication(appWithTestDatabase) {
      db.challenge.clear()

      val newStatus = ChallengeStatus.Rejected
      val newOpponentSolutionId = "opponentId"
      val challenge = createChallengeStub(status = ChallengeStatus.Accepted)

      db.challenge.create(challenge)
      db.challenge.updateChallenge(
        id = challenge.id,
        newStatus = newStatus,
        opponentSolutionId = Some(newOpponentSolutionId))

      val maybeChallenge = db.challenge.readById(challenge.id)

      maybeChallenge must beSome
      maybeChallenge.get.status must beEqualTo(newStatus)
      maybeChallenge.get.opponentSolutionId must beEqualTo(Some(newOpponentSolutionId))
    }

    "Removes participant" in new WithApplication(appWithTestDatabase) {
      db.conversation.clear()

      val ps = List("1", "2")
      val conv = createConversationStub(pIds = ps)
      db.conversation.create(conv)

      db.conversation.removeParticipant(conv.id, ps(0))

      val c = db.conversation.readById(conv.id).get

      c.participants.size must beEqualTo(1)
    }
  }
}

