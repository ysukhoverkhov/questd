package models.domain

case class TutorialState(
    clientTutorialState: Map[TutorialPlatform.Value, String] = Map(),
    assignedTutorialTaskIds: List[String] = List.empty
)

