package logic

import components._
import models.domain._

trait LogicBootstrapper { this: APIAccessor with RandomAccessor =>

  import scala.language.implicitConversions

  implicit def user2Logic(user: User): UserLogic = new UserLogic(user, api, rand)

  implicit def quest2Logic(quest: Quest): QuestLogic = new QuestLogic(quest, api)

  implicit def questSolution2Logic(qs: Solution): QuestSolutionLogic = new QuestSolutionLogic(qs, api)
}
