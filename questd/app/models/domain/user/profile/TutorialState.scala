package models.domain.user.profile

/**
 * User's state of his own tutorial.
 */
case class TutorialState(
  closedElementIds: List[String] = List.empty,
  usedTutorialTaskIds: List[String] = List.empty,
  usedTutorialQuestIds: List[String] = List.empty,
  dailyTasksSuppression: Boolean = true
  )
