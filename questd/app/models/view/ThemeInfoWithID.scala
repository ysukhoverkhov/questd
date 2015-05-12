package models.view

import models.domain._
import models.domain.tag.ThemeInfo

// TODO: remove me with tags.
case class ThemeInfoWithID (
    id: String,
    obj: ThemeInfo)
