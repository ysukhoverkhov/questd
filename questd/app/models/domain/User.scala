package models.domain

import models.domain.base.ID

/**
 * Structure representing beloved user.
 */
case class User(
  id: String = ID.generateUUID(),
  auth: AuthInfo = AuthInfo(),
  profile: Profile = Profile(),
  schedules: UserSchedules = UserSchedules(),
  stats: UserStats = UserStats(),
  privateDailyResults: List[DailyResult] = List()) extends ID

