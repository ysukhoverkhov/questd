package models.domain.common

/**
 * Defines type of content.
 */
object ContentType extends Enumeration {
   type ContentType = Value

   val Photo = Value(0)
   val Video = Value(1)
 }
