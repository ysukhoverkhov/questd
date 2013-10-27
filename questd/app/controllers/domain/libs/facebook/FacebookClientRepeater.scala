package controllers.domain.libs.facebook

import com.restfb._
import com.restfb.exception._
import play.Logger
import com.restfb._

private[facebook] class FacebookClientRepeater(val client: FacebookClient) {

  /**
   * Fetches object repeating call three times
   */
  def fetchObject[T](obj: String, objectType: Class[T], parameters: Parameter*): T = {

    val requestResult = List.range(0, 3).foldLeft[(Option[T], Option[FacebookNetworkException])]((None, None)) { (c, i) =>
      Logger.debug("Making request #" + i + " to FB")
      c match {
        case (Some(r), e) => (Some(r), e)
        case (None, _) => {
          try {
            (Some(client.fetchObject(obj, objectType, parameters: _*)), None)
          } catch {
            case ex: FacebookNetworkException => {
              Logger.debug("Request to FB failed.")
              (None, Some(ex))
            }
          }
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

