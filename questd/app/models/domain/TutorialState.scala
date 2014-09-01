package models.domain

case class TutorialState(
    clientTutorialState: Map[String, String] = Map(),
    assignedTutorialTaskIds: List[String] = List()
)

