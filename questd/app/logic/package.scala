
import scala.language.implicitConversions
import models.domain._

package object logic {

  implicit def user2Logic(user: User): UserLogic = new UserLogic(user)
  
  implicit def quest2Logic(quest: Quest): QuestLogic = new QuestLogic(quest)
}
