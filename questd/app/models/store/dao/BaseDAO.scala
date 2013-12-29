package models.store.dao

private[store] trait BaseDAO[T] {
  def create(u: T): Unit
  def readByID(key: String): Option[T]
  // TODO: remove me?
  def update(u: T): Unit
  def upsert(o: T): Unit
  def delete(id: String): Unit
  def all: Iterator[T]
}

