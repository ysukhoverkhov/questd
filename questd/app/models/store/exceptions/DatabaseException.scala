package models.store.exceptions

private[store] class DatabaseException (
  m: String,
  th: Throwable,
  val silent: Boolean) extends RuntimeException(m, th) {

  def this(ex: Exception) = this("", ex, false)

  def this(m: String) = this(m, null, false)

  def this(silent: Boolean) = this("", null, silent)
}

