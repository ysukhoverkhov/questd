package models.store.mongo.dao

import com.mongodb.casbah.MongoClient
import models.domain.user.User
import models.store.mongo.helpers.QSalatDAO
import org.bson.types.ObjectId
import models.store.mongo.SalatContext._

class MongoUserDAOForTest extends MongoUserDAO {
  override val dao = new QSalatDAO[User, ObjectId](collection = MongoClient("localhost", 55555)("test_db")("test_coll")) {}

}

