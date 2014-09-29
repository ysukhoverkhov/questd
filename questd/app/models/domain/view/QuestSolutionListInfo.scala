package models.domain.view

case class QuestSolutionListInfo (
    val solution: QuestSolutionInfoWithID,
    val quest: Option[QuestInfoWithID],
    val author: Option[PublicProfileWithID]
)