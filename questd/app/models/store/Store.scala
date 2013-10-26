package models.store

import models.store.anorm._
import models.store.mongo._
// TODO remove me.
/*
private[store] object store {

  abstract class Store {
    protected[store] val db: Database
  }

  object Store extends Store {
    protected[store] val db: Database = { MongoDatabase }
  }

  import scala.language.implicitConversions
  implicit def StoreToHoldingDatabase(s: Store): Database = s.db
  
}

*/