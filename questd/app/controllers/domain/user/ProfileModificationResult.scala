package controllers.domain.user

object ProfileModificationResult extends Enumeration {
  type ProfileModificationResult = Value
  val OK = Value(0)
  val LevelTooLow = Value(1)
  val NotEnoughCoins = Value(2)
  val NotEnoughMoney = Value(3)
}