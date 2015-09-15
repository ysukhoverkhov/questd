package models.view

import models.domain._
import models.domain.user.profile.PublicProfile

case class ProfileView(
    id: String,
    info: PublicProfile)
