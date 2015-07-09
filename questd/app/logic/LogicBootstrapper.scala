package logic

import components._
import models.domain._
import models.domain.battle.Battle
import models.domain.quest.Quest
import models.domain.solution.Solution
import models.domain.user.User

trait LogicBootstrapper { this: APIAccessor with RandomAccessor =>

  import scala.language.implicitConversions

  implicit def user2Logic(user: User): UserLogic = new UserLogic(user, api, rand)

  implicit def quest2Logic(quest: Quest): QuestLogic = new QuestLogic(quest, api)

  implicit def solution2Logic(qs: Solution): SolutionLogic = new SolutionLogic(qs, api)

  implicit def battle2Logic(battle: Battle): BattleLogic = new BattleLogic(battle, api)
}
