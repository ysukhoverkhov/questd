package models.store

import models.store.anorm._

object store {

  abstract class Store {
    protected[store] val db: Database
  }

  object Store extends Store {
    protected[store] val db: Database = { AnormDatabase }
  }

  import scala.language.implicitConversions
  implicit def StoreToHoldingDatabase(s: Store): Database = s.db
  
}

