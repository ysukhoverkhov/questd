package models.domain.user.devices

import models.domain.common.ClientPlatform

/**
 * Registered device of a user.
 *
 * Created by Yury on 17.08.2015.
 */
case class Device (
  platform: ClientPlatform.Value,
  token: String
  )
