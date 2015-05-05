package models.store.dao

import models.domain.base.ID

private[store] trait BaseDAO[T <: ID] {
  def create(u: T): Unit
  def readById(key: String): Option[T]
  def readManyByIds(ids: List[String], skip: Int = 0): Iterator[T]
  def update(u: T): Unit
  def upsert(o: T): Unit
  def delete(id: String): Unit
  def all: Iterator[T]
}

