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
  private def selectObject[T,C](
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
    if (l.length > 0) {

      // This is required since salat makes embedded lists as BasicDBLists.
      if (l.head.getClass() == classOf[BasicDBList]) {
        val rv = for (
          out <- l.asInstanceOf[List[BasicDBList]];
          in <- out.toArray().asInstanceOf[Array[Object]];
          if getQuestIdInReference(in.asInstanceOf[C]) == s
        ) yield {
          true
        }
        
      rv.length > 0
        
      } else {
        // if we are dealing not with sic BasicDBList just do it in a nice way.
        l.flatten.contains(s)
      }

    } else {
      // False if length of array is 0. This is required since we need to check type of first element in the array.
      false
    }
  }

}