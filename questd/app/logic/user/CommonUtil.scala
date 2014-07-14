package logic.user

import logic.UserLogic
import com.mongodb.BasicDBList
import models.domain._
import com.mongodb.BasicDBObject

trait CommonUtil { this: UserLogic =>

  /**
   * Select quest what is not or quest and not in given list.
   */
  private[user] def selectQuest(
    i: Iterator[Quest],
    usedQuests: List[List[String]]): Option[Quest] = {

    selectObject[Quest, String](
      i,
      (_.id),
      (_.authorUserId),
      usedQuests,
      ((x: String) => x))
  }

  private[user] def selectQuestSolution(
    i: Iterator[QuestSolution],
    usedQuests: List[List[String]]): Option[QuestSolution] = {

    selectObject[QuestSolution, String](
      i,
      (_.id),
      (_.userId),
      usedQuests,
      ((x: String) => x))
  }

  /**
   * Select object what is not our and not in list of lists.
   */
  private def selectObject[T, C](
    i: Iterator[T],
    getQuestId: (T => String),
    getQuestAthorId: (T => String),
    usedQuests: List[List[C]],
    getQuestIdInReference: (C => String)): Option[T] = {
    if (i.hasNext) {
      val q = i.next()

      if (getQuestAthorId(q) != user.id
        && !(listOfListsContainsString(usedQuests, getQuestIdInReference, getQuestId(q)))) {
        Some(q)
      } else {
        selectObject(i, getQuestId, getQuestAthorId, usedQuests, getQuestIdInReference)
      }
    } else {
      None
    }
  }

  /**
   * Check is string in list of dblists of strings.
   */
  private[user] def listOfListsContainsString[C](l: List[List[C]], getQuestIdInReference: (C => String), s: String) = {
    import models.store.mongo.helpers._
    l.mongoFlatten.map(getQuestIdInReference).contains(s)
  }
}
