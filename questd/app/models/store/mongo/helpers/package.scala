package models.store.mongo

import com.mongodb.MongoException
import models.store.exceptions.DatabaseException
import com.novus.salat.dao.ModelCompanion
import com.mongodb.DBObject
import com.mongodb.casbah.Imports._

package object helpers {

  /**
   * Various helper mix ins.
   */
  trait BaseDao[T] { this: ModelCompanion[T, ObjectId] =>
    
    /**
     * Makes DBObject for query
     */
    protected def makeQueryObject(o: T): DBObject = {
      // TODO OPTIMIZATION replace it with custom object to map converter what makes unlifting and removes "None" from the map completelly.

      val js = toCompactJson(o)
      val dbo = com.mongodb.util.JSON.parse(js).asInstanceOf[DBObject]
      
      dbo
    }
 
    
    /**
     * Create
     */
    def create(o: T): Unit = wrapMongoException {

      val wr = save(o)
      if (!wr.getLastError().ok) {
        throw new DatabaseException(wr.getLastError().getErrorMessage())
      }
    }

    
    /**
     * All objects
     */
    def all: List[T] = wrapMongoException {
      // TODO OPTIMIZATION this will be very slow and will fetch everything.
      List() ++ find(MongoDBObject())
    }

  }

  
  
  /**
   * Wrapper for handling unknown exceptions.
   */
  private[mongo] def wrapMongoException[T](f: => T): T = try {
    f
  } catch {
    case ex: MongoException => {
      throw new DatabaseException(ex)
    }
  }

}

