package models.domain.user

/**
 * User's state of his own tutorial.
 */
case class TutorialState(
    closedElementIds: List[String] = List.empty,
    usedTutorialTaskIds: List[String] = List.empty
)
