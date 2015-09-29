package logic

import controllers.domain.DomainAPIComponent
import models.domain.challenge.{Challenge, ChallengeStatus}

class ChallengeLogic(
  val challenge: Challenge,
  val api: DomainAPIComponent#DomainAPI) {

  /**
   * Should the challenge be auto rejected
   */
  def shouldBeAutoRejected = {
    import com.github.nscala_time.time.Imports._
    import org.joda.time.DateTime

    val daysToWait = api.config(api.DefaultConfigParams.RequestsAutoRejectDays).toInt

    (challenge.status == ChallengeStatus.Requested) &&
      (new DateTime(challenge.creationDate) + daysToWait.days < DateTime.now)
  }
}

