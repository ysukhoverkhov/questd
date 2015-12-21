package models.store.mongo

import com.novus.salat._
import play.api.Play.current
import play.api._

private[mongo] object SalatContext {
  implicit val ctx = {
    val c = new Context() {
      val name = "Custom Context"
    }
    c.registerClassLoader(Play.classloader)
    c
  }
}
