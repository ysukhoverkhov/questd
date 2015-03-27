package helpers.rich

class RichBoolean (val v: Boolean) {

  def ^^(o: Boolean) = {
    (v || o) && !(v && o)
  }

}
