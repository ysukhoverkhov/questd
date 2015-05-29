package models.domain.user.dailyresults

import models.domain.common.Assets

/**
 * Income for a single quest per day.
 * @param questId Id of a quest income calculated for.
 * @param passiveIncome Passive income we receive just because we have a quest.
 * @param timesLiked Number of times our quest was liked since creation.
 * @param likesIncome Income for quest likes.
 * @param timesSolved Number of times our quest was solved today.
 * @param solutionsIncome Income for solving our quest.
 */
case class QuestIncome(
  questId: String,
  passiveIncome: Assets,
  timesLiked: Int,
  likesIncome: Assets,
  timesSolved: Int = 0,
  solutionsIncome: Assets = Assets())
