package controllers.domain.user.protocol

object ProfileModificationResult extends Enumeration {
  type ProfileModificationResult = Value

  val OK = Value(0, "0")
  val LevelTooLow = Value(1, "1")
  val NotEnoughCoins = Value(2, "2")
  val NotEnoughMoney = Value(3, "3")
}

