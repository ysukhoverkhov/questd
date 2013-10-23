package controllers.domain.base

import models.domain.user.SessionID
import models.domain.user.UserID
  
private[domain] object base {

  // TODO: check is ti used.
  abstract class AuthorizedAPIRequestParams {
    val sessionId: SessionID
    val userId: UserID
  }
  
  
}

