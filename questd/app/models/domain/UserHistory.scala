package models.domain


/**
 * History of user dids. Outer list is a day, inner list is a did in a day.
 * We store here 2, 2 lists since salat serializes them incorrectly in other case.
 */
case class UserHistory(

  /**
   * Set of themes we select for quests for for solving.
   */
  themesOfSelectedQuests: List[String] = List(),

  /**
   * Set of themes we selected for proposing quests.
   */
  selectedThemeIds: List[String] = List()
  )

