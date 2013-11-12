package controllers.domain.user.protocol

object ProfileModificationResult extends Enumeration {
  type ProfileModificationResult = Value

  val OK = Value(0, "0")
  val LevelTooLow = Value(1, "1")
  val NotEnoughAssets = Value(2, "2")
}

