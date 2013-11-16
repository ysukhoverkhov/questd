package models.domain

import scala.language.implicitConversions
import models.domain.base.ID

case class User (
  id: String = ID.generateUUID(),
  auth: AuthInfo = AuthInfo(),
  profile: Profile = Profile()) extends ID

