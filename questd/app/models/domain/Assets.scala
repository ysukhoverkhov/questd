package models.domain

/**
 * Represents cost of everything in the game.
 */
case class Assets(
  val coins: Int = 0,
  val money: Int = 0,
  val rating: Int = 0) {

  def -(o: Assets): Assets =
    Assets(this.coins - o.coins,
      this.money - o.money,
      this.rating - o.rating)

  def +(o: Assets): Assets =
    Assets(this.coins + o.coins,
      this.money + o.money,
      this.rating + o.rating)

  def *(m: Int): Assets =
    Assets(this.coins * m,
      this.money * m,
      this.rating * m)

  def canAfford(o: Assets): Boolean =
    this.coins >= o.coins && this.money >= o.money && this.rating >= o.rating

  def clampBot: Assets = clampBot(Assets(0, 0, 0))

  def clampBot(other: Assets): Assets = {
    val c = if (coins < other.coins) other.coins else coins
    val m = if (money < other.money) other.money else money
    val r = if (rating < other.rating) other.rating else rating

    Assets(c, m, r)
  }

  def clampTop: Assets = clampTop(Assets(0, 0, 0))

  def clampTop(other: Assets): Assets = {
    val c = if (coins > other.coins) other.coins else coins
    val m = if (money > other.money) other.money else money
    val r = if (rating > other.rating) other.rating else rating

    Assets(c, m, r)
  }
}
    
