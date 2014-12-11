package models.domain


case class QuestSolutionInfoContent(
  media: ContentReference,
  icon: Option[ContentReference] = None)

case class QuestSolutionInfo(
  content: QuestSolutionInfoContent,
  vip: Boolean,
  authorId: String,
  questId: String)
