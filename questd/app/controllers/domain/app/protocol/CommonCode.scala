package controllers.domain.app.protocol


trait CommonCode { this: Enumeration =>
  /**
   * Modification has been made.
   */
  val OK = Value

  /**
   * We do not have rights to perform this actions.
   */
  val NotEnoughRights = Value

  /**
   * Not enough assets to perform the action (not enough money or coins)
   */
  val NotEnoughAssets = Value

  /**
   * Profile is incomplete and this call is not allowed because of this.
   */
  val IncompleteBio = Value
}

