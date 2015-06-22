package logic.user

import logic.UserLogic
import logic.constants._
import logic.functions._
import models.domain.user.profile.{Functionality, Rights}

/**
 * All logic related to calculating user rights.
 */
trait CalculatingRights { this: UserLogic =>

  def calculateRights: Rights = {
    Rights(
      unlockedFunctionality = restrictions.foldLeft(Set[Functionality.Value]()) { case (c, (right, level)) => if (level <= user.profile.publicProfile.level) c + right else c },
      maxFriendsCount = maxNumberOfFriendsOnLevel(user.profile.publicProfile.level))
  }

}

