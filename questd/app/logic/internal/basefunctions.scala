package logic.internal

private [logic] object basefunctions {
  
  /**
   * Our super mega function what rules all curves
   */
  def megaf(level: Int, k: Double, d: Double, b: Double, y: Double) = k * math.exp((level - 1) / d) + y * level + b

  /**
   * Function used to calculate various rewards.
   */
  def rewardFunction(x: Double) = {
    val r0 = 3.56295
    val r1 = -0.83827
    val r2 = -1.08076
    
    def rewardFunctionInt(x: Double, k: Double, d: Double, b: Double) = {
      k * math.exp(x / d) + b 
    }
    
    rewardFunctionInt(x, r0, r1, r2)
  } 
  
}
