package models.domain.culture

import models.domain.base.ID

/**
 * These objects hold public personalized information.
 */
case class Culture(
  id: String = ID.generate,
  name: String,
  countries: List[String] = List.empty) extends ID
