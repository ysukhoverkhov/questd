package models.store.mongo

import com.novus.salat._
import play.api._
import play.api.Play.current

private[mongo] object SalatContext {
  implicit val ctx = {
    val c = new Context() {
      val name = "Custom Context"
    }
    c.registerClassLoader(Play.classloader)
    c
  }
}
