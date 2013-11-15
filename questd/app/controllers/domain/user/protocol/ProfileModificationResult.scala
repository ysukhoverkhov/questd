package controllers.domain.user.protocol

object ProfileModificationResult extends Enumeration {
  type ProfileModificationResult = Value

  /**
   * Modification has been made.
   */
  val OK = Value(0, "OK")
  
  /**
   * Level too low to perform the action.
   */
  val LevelTooLow = Value(1, "LevelTooLow")
  
  /**
   * Not enough assets to perform the action (not enough money or coins)
   */
  val NotEnoughAssets = Value(2, "NotEnoughAssets")
  
  /**
   * This actions can't be performed since to little time has passed since last one.
   */
  val CoolDown = Value(3, "CoolDown")
  
  /**
   * Invalid state. For example we try to take quest theme if no themes purchased.
   */
  val InvalidState = Value(4, "InvalidState")
}

