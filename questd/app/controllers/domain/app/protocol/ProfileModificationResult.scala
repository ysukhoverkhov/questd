package controllers.domain.app.protocol

object ProfileModificationResult extends Enumeration {
  type ProfileModificationResult = Value

  /**
   * Modification has been made.
   */
  val OK = Value

  /**
   * Profile is incomplete and this call is not allowed because of this.
   */
  val IncompleteBio = Value

  /**
   * We do not have rights to perform this actions.
   */
  val NotEnoughRights = Value

  /**
   * Not enough assets to perform the action (not enough money or coins)
   */
  val NotEnoughAssets = Value

  /**
   * This actions can't be performed since to little time has passed since last one.
   */
  val CoolDown = Value

  /**
   * Invalid state. For example we try to take quest theme if no themes purchased.
   */
  val InvalidState = Value

  /**
   * Used when we do not have enough content to fulfill the request.
   */
  val OutOfContent = Value

  /**
   * Used in array operators if request will result in array with size above the limit.
   */
  val LimitExceeded = Value

  /**
   * Tutorial task already assigned.
   */
  val AlreadyAssigned = Value
}

