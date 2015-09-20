

package models.store.mongo

import models.domain.challenge.{Challenge, ChallengeStatus}
import org.specs2.mutable._
import play.api.test._

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
        Challenge(mySolutionId = Some(mySolutionId), opponentSolutionId = Some(opponentSolutionId), myId = "", opponentId = "", status = ChallengeStatus.Accepted),
        Challenge(mySolutionId = None, opponentSolutionId = Some(opponentSolutionId), myId = "", opponentId = "", status = ChallengeStatus.Accepted),
        Challenge(mySolutionId = Some(mySolutionId), opponentSolutionId = None, myId = "", opponentId = "", status = ChallengeStatus.Accepted)
      )

      challenges.foreach(db.challenge.create)

      db.challenge.findBySolutions((mySolutionId, opponentSolutionId)).toList must beEqualTo(List(challenges.head))
      db.challenge.findBySolutions((opponentSolutionId, mySolutionId)).toList must beEqualTo(List(challenges.head))
    }
  }
}

