package controllers.domain.app.protocol

object ProfileModificationResult extends Enumeration {
  type ProfileModificationResult = Value

  /**
   * Modification has been made.
   */
  val OK = Value(0)
  
  /**
   * We do not have rights to perform this actions.
   */
  val NotEnoughRights = Value(1)
  
  /**
   * Not enough assets to perform the action (not enough money or coins)
   */
  val NotEnoughAssets = Value(2)
  
  /**
   * This actions can't be performed since to little time has passed since last one.
   */
  val CoolDown = Value(3)
  
  /**
   * Invalid state. For example we try to take quest theme if no themes purchased.
   */
  val InvalidState = Value(4)

  /**
   * Used when we do not have enough content to fulfill the request. 
   */
  val OutOfContent = Value(5)
}

