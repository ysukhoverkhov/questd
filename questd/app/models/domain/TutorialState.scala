package models.domain

case class TutorialState(
    closedElementIds: List[String] = List.empty,
    assignedTutorialTaskIds: List[String] = List.empty
)

