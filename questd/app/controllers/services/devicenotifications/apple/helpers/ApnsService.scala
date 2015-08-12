package controllers.services.devicenotifications.apple.helpers

import com.notnoop.apns.{APNS, ApnsService}

/**
 * APNS Service creator.
 *
 * Created by Yury on 12.08.2015.
 */
trait APNSService
{
  // TODO: read password from config file.
  // TODO: store in correct place.
  val service: ApnsService =
    APNS.newService()
      .withCert("d:/QMPushDevelop.p12", "123")
      .withSandboxDestination()
      .build()

}
