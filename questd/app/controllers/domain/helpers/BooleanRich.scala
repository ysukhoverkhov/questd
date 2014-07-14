package controllers.domain.helpers

class BooleanRich (val v: Boolean) {

  def ^^(o: Boolean) = {
    (v || o) && !(v && o)
  }

}
