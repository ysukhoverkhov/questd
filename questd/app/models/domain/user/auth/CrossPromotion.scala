package models.domain.user.auth

/**
 * Cross promotion info.
 */
case class CrossPromotion (
  otherApps: List[CrossPromotedApp] = List.empty
  )
