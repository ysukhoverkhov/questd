package models.domain

import models.domain.base.ID


/**
 * These objects hold public personalized information.
 */
case class Culture(
  id: String = ID.generateUUID(),
  name: String,
  countries: List[String] = List()) extends ID
    