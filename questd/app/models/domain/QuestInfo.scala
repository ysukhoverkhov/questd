package models.domain

case class QuestInfoContent(
  media: ContentReference,
  icon: Option[ContentReference],
  description: String)

case class QuestInfo(
  authorId: String,
  content: QuestInfoContent,
  level: Int,
  vip: Boolean,
  solveCost: Assets,
// TODO: remove me default value.
  solveRewardWon: Assets = Assets(),
  solveRewardLost: Assets = Assets())

