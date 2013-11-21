package models.domain

import models.domain.base.ID

case class User (
  id: String = ID.generateUUID(),
  auth: AuthInfo = AuthInfo(),
  profile: Profile = Profile(),
  schedules: UserSchedules = UserSchedules()) extends ID

