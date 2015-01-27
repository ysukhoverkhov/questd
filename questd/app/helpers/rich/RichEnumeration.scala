package helpers.rich

/**
 * Makes work with enumeration more comfortable.
 */
class RichEnumeration[T <: Enumeration] (val v: T) {

  /**
   * Get value by name signaling with None and not exception about missing one.
   * @param name Name of a value.
   * @return Some(value) if it exists in enumeration or None if it does not.
   */
  def withNameOption(name: String): Option[v.Value] = {
    try {
      Some(v.withName(name))
    }
    catch {
      case ex: java.util.NoSuchElementException => None
    }
  }

  def withNameEx(name: String): v.Value = {
    try {
      v.withName(name)
    }
    catch {
      case ex: java.util.NoSuchElementException =>
        throw new java.util.NoSuchElementException(s"""Incorrect enum name "$name\"""")
    }
  }
}
