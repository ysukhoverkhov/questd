package controllers.services.socialnetworks.facebook

import com.restfb._
import com.restfb.exception._
import play.Logger

private[facebook] class FacebookClientRepeater(private val client: FacebookClient) {

  /**
   * Fetches object repeating call three times
   */
  def fetchObject[T](obj: String, objectType: Class[T], parameters: Parameter*): T = {
    requestSeveralTimes(client.fetchObject(obj, objectType, parameters: _*))
  }

  /**
   * Fetch connection from FB.
   */
  def fetchConnection[T](obj: String, objectType: Class[T], parameters: Parameter*): Connection[T] = {
    requestSeveralTimes(client.fetchConnection(obj, objectType, parameters: _*))
  }

  /**
   * deletes object on FB
   * @param obj id of object to delete
   */
  def deleteObject(obj: String): Unit = {
    requestSeveralTimes(client.deleteObject(obj))
  }

  /**
   * Executes FQL query on FB
   */
  def executeFqlQuery[T](query: String, objectType: Class[T], parameters: Parameter*): java.util.List[T] = {
    requestSeveralTimes(client.executeFqlQuery(query, objectType, parameters: _*))
  }

  /**
   * Repeat request several times.
   */
  private def requestSeveralTimes[T](fun: => T): T = {
    val requestResult = List.range(0, 3).foldLeft[(Option[T], Option[FacebookNetworkException])]((None, None)) { (c, i) =>
      c match {
        case (Some(r), e) => (Some(r), e)
        case (None, _) =>
          try {
            (Some(fun), None)
          } catch {
            case ex: FacebookNetworkException =>
              Logger.debug("Request to FB failed.")
              (None, Some(ex))
          }
      }
    }

    requestResult match {
      case (Some(r), _) => r
      case (None, Some(ex)) => throw ex
      case (None, None) => throw new RuntimeException("Error in repeater logic, nothing returned")
    }
  }
}

