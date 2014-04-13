package logic.user

import logic.UserLogic
import com.mongodb.BasicDBList

trait CommonUtil { this: UserLogic =>

  /**
   * Select quest what is not or quest and not in given list.
   */
  private[user] def selectQuest[T](i: Iterator[T], fid: (T => String), fauthorid: (T => String), usedQuests: List[List[String]]): Option[T] = {
    if (i.hasNext) {
      val q = i.next()

      if (fauthorid(q) != user.id
        && !(listOfListsContainsString(usedQuests, fid(q)))) {
        Some(q)
      } else {
        selectQuest(i, fid, fauthorid, usedQuests)
      }
    } else {
      None
    }
  }

  /**
   * Check is string in list of dblists of strings.
   */
  private def listOfListsContainsString(l: List[List[String]], s: String) = {
    if (l.length > 0) {

      // This is required since salat makes embedded lists as BasicDBLists.
      val rv = if (l.head.getClass() == classOf[BasicDBList]) {
        for (
          out <- l.asInstanceOf[List[BasicDBList]];
          in <- out.toArray();
          if in == s
        ) yield {
          true
        }
      } else {
        for (
          out <- l;
          in <- out;
          if in == s
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