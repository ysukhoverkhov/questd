package models.domain


case class SolutionInfoContent(
  media: ContentReference,
  icon: Option[ContentReference] = None)

case class SolutionInfo(
  content: SolutionInfoContent,
  vip: Boolean,
  authorId: String,
  questId: String)
