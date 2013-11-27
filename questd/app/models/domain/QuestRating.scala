package models.domain

case class QuestRating(
  points: Int = 0,
  cheatingPoints: Int = 0,
  iacrating: IAContentRating = IAContentRating())
