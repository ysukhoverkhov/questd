package models.domain.user.auth

/**
 * Cross promotion info.
 */
case class CrossPromotion (
  apps: List[CrossPromotedApp] = List.empty
  )
