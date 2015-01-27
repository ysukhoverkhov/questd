package helpers

package object rich {

  import scala.language.implicitConversions
  implicit def enrichBoolean(v: Boolean): RichBoolean = new RichBoolean(v)

}
