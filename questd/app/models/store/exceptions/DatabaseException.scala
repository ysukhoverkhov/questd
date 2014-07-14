package models.store.exceptions

private[store] class DatabaseException (m: String, th: Throwable) extends RuntimeException(m, th) {

  def this(ex: Exception) = this("", ex) 

  def this(m: String) = this(m, null) 
}

