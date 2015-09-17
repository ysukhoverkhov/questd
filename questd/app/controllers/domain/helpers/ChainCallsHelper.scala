package controllers.domain.helpers

/**
 * helps to perform calls in chain.
 */
private [helpers] object ChainCallsHelper {

  def runWhileSome[T](subject: T)(functions: Seq[(T) => Option[T]]): Option[T] = {
    functions.foldLeft[Option[T]](Some(subject))((r, f) => r.flatMap(f))
  }

}
