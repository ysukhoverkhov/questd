package models.view

import models.domain._
import models.domain.user.PublicProfile

case class ProfileView(
    id: String,
    info: PublicProfile)
