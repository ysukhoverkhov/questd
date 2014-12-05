package models.domain.view

case class SolutionListInfo (
  solution: SolutionInfoWithID,
  quest: Option[QuestInfoWithID],
  author: Option[PublicProfileWithID]
  )
