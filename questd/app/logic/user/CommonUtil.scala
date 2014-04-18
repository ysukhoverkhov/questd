package logic.user

import logic.UserLogic
import com.mongodb.BasicDBList
import models.domain._

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
        (_.authorUserID),
        usedQuests,
        ((x: String) => x))
  }

  private[user] def selectQuestSolution(
      i: Iterator[QuestSolution], 
      usedQuests: List[List[String]]): Option[QuestSolution] = {
    
    selectObject[QuestSolution, String](
        i,
        (_.id),
        (_.userID),
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
  
// TODO: write test for me.
  /**
   * Check is string in list of dblists of strings.
   */
  private def listOfListsContainsString[C](l: List[List[C]], getQuestIdInReference: (C => String), s: String) = {
    if (l.length > 0) {

      // This is required since salat makes embedded lists as BasicDBLists.
      val rv = if (l.head.getClass() == classOf[BasicDBList]) { // TODO: replace here object with string or find a way how to deserialize with correct objects.
        for ( // TODO rewrite me with flat map after test.
          out <- l.asInstanceOf[List[BasicDBList]];
          in <- out.toArray().asInstanceOf[Array[C]];
          if getQuestIdInReference(in) == s
        ) yield {
          true
        }
      } else {
        for ( // TODO rewrite me with flat map after test.
          out <- l;
          in <- out;
          if getQuestIdInReference(in) == s
        ) yield {
          true
        }
      }

      rv.length > 0

    } else {
      false
    }
  }

}