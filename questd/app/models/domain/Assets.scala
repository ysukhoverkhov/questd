package models.domain

/**
 * Represents cost of everything in the game.
 */
case class Assets(
  val coins: Int,
  val money: Int,
  val rating: Int) {

  def -(o: Assets): Assets =
    Assets(this.coins - o.coins,
      this.money - o.money,
      this.rating - o.rating)

  def +(o: Assets): Assets =
    Assets(this.coins + o.coins,
      this.money + o.money,
      this.rating + o.rating)

}
    
