package controllers.services.devicenotifications.apple.helpers

import com.notnoop.apns.{APNS, ApnsService}
import play.Play

/**
 * APNS Service creator.
 *
 * Created by Yury on 12.08.2015.
 */
trait APNSService
{
  val service: ApnsService =
    APNS.newService()
      .withCert("conf/QMPushDevelop.p12", Play.application().configuration().getString("questd.devicenotifications.apple.certificatepass"))
      .withProductionDestination()
      .build()
}
