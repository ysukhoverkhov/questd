package logic.internal

private [logic] object basefunctions {
  
  /**
   * Our super mega function what rules all curves
   */
  def megaf(level: Int, k: Double, d: Double, b: Double, y: Double) = k * math.exp((level - 1) / d) + y * level + b

}
