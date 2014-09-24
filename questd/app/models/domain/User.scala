package models.domain

import models.domain.base.ID

/**
 * Structure representing beloved user.
 */
case class User(
  id: String = ID.generateUUID(),
  auth: AuthInfo = AuthInfo(),
  demo: UserDemographics = UserDemographics(),
  profile: Profile = Profile(),
  schedules: UserSchedules = UserSchedules(),
  stats: UserStats = UserStats(),
  privateDailyResults: List[DailyResult] = List(),
  history: UserHistory = UserHistory(),
  shortlist: List[String] = List(),
  friends: List[Friendship] = List(),
  mustVoteSolutions: List[String] = List(),
  messages: List[Message] = List(),
  tutorial: TutorialState = TutorialState(),
  payedAuthor: Boolean = false) extends ID

