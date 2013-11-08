
import scala.language.implicitConversions
import models.domain._

package object logic {

  //type UserLogic = logic.UserLogic
  implicit def user2Logic(user: User): UserLogic = new UserLogic(user)
  
}
