package models.domain.base


private[domain] abstract class BaseID[T] {
  val id: T
  
  override def toString = id.toString

}
