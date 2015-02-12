package models.domain.view

// TODO: remove me since it's obsolete with SolutionView
case class SolutionListInfo (
  solution: SolutionView,
  quest: Option[QuestView],
  author: Option[ProfileView]
  )
