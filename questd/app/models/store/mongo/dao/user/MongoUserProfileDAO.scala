package models.store.mongo.dao.user
import com.mongodb.casbah.commons.MongoDBObject
import com.novus.salat._
import models.domain.common.Assets
import models.domain.user.User
import models.domain.user.profile.Rights
import models.store.dao.user.UserProfileDAO
import models.store.mongo.helpers.BaseMongoDAO
import models.store.mongo.SalatContext._

/**
 * Mongo DAO implementation.
 */
trait MongoUserProfileDAO extends UserProfileDAO {
  this: BaseMongoDAO[User] =>

  /**
   * @inheritdoc
   */
  def addToAssets(id: String, assets: Assets): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$inc" -> MongoDBObject(
          "profile.assets.coins" -> assets.coins,
          "profile.assets.money" -> assets.money,
          "profile.assets.rating" -> assets.rating)))
  }

  /**
   * @inheritdoc
   */
  def levelUp(id: String, ratingToNextLevel: Int): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$inc" -> MongoDBObject(
          "profile.publicProfile.level" -> 1,
          "profile.assets.rating" -> -ratingToNextLevel)))
  }

  /**
   * @inheritdoc
   */
  def setNextLevelRatingAndRights(id: String, newRatingToNextLevel: Int, rights: Rights): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$set" -> MongoDBObject(
          "profile.ratingToNextLevel" -> newRatingToNextLevel,
          "profile.rights" -> grater[Rights].asDBObject(rights))))
  }

  /**
   * @inheritdoc
   */
  def setGender(id: String, gender: String): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$set" -> MongoDBObject(
          "profile.publicProfile.bio.gender" -> gender)))
  }

  /**
   * @inheritdoc
   */
  def setDebug(id: String, debug: String): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$set" -> MongoDBObject(
          "profile.debug" -> debug)))
  }

  /**
   * @inheritdoc
   */
  def setCity(id: String, city: String): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$set" -> MongoDBObject(
          "profile.publicProfile.bio.city" -> city)))
  }

  /**
   * @inheritdoc
   */
  def setCountry(id: String, country: String): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$set" -> MongoDBObject(
          "profile.publicProfile.bio.country" -> country)))
  }

  /**
   * @inheritdoc
   */
  def updateCultureId(id: String, cultureId: String): Option[User] = {
    findAndModify(
      id,
      MongoDBObject(
        "$set" -> MongoDBObject(
          "demo.cultureId" -> cultureId)))
  }

  /**
   * @inheritdoc
   */
  def replaceCultureIds(oldCultureId: String, newCultureId: String): Unit = {
    update(
      query = MongoDBObject(
        "demo.cultureId" -> oldCultureId),
      updateRules = MongoDBObject(
        "$set" -> MongoDBObject(
          "demo.cultureId" -> newCultureId)),
      multi = true)
  }
}
