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

  def canAfford(o: Assets): Boolean =
    this.coins >= o.coins && this.money >= o.money && this.rating >= o.rating
}
    
