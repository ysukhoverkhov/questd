package models.domain.user

import models.domain.base.ID
import models.domain.user.auth.AuthInfo
import models.domain.user.dailyresults.DailyResult

/**
 * Structure representing beloved user.
 */
case class User(
  id: String = ID.generateUUID(),
  auth: AuthInfo = AuthInfo(),
  demo: UserDemographics = UserDemographics(),
  profile: Profile = Profile(),
  privateDailyResults: List[DailyResult] = List.empty,
  timeLine: List[TimeLineEntry] = List.empty,
  schedules: UserSchedules = UserSchedules(),
  stats: UserStats = UserStats(),
  following: List[String] = List.empty,
  followers: List[String] = List.empty,
  friends: List[Friendship] = List.empty,
  mustVoteSolutions: List[String] = List.empty,
  payedAuthor: Boolean = false) extends ID
