package models.domain.user.auth

/**
 * Single login method user has used.
 */
case class LoginMethod (
  /// Name of login method
  methodName: String,

  /// User's id in login method
  userId: String,

  /// Info with other our apps the user is playing in.
  crossPromotion: CrossPromotion = CrossPromotion()
  )
