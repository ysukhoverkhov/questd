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
  privateDailyResults: List[DailyResult] = List(),
  privateTimeLine: List[TimeLineEntry] = List(),
  schedules: UserSchedules = UserSchedules(),
  stats: UserStats = UserStats(),
  history: UserHistory = UserHistory(),
  shortlist: List[String] = List(),
  friends: List[Friendship] = List(),
  mustVoteSolutions: List[String] = List(),
  messages: List[Message] = List(),
  tutorial: TutorialState = TutorialState(),
  payedAuthor: Boolean = false) extends ID

