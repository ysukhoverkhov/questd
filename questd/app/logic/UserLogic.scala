package logic

import java.util.Date
import scala.util.Random
import com.github.nscala_time.time.Imports._
import org.joda.time.DateTime
import models.domain._
import models.domain.base._
import models.domain.ContentType._
import controllers.domain.app.protocol.ProfileModificationResult._
import controllers.domain.OkApiResult
import controllers.domain.DomainAPIComponent
import components.componentregistry.ComponentRegistrySingleton
import functions._
import constants._
import play.Logger
import controllers.domain.admin._
import com.mongodb.BasicDBList
import models.store.dao.ThemeDAO
import components.random.RandomComponent
import logic.user._

// This should not go to DB directly since API may have cache layer.
class UserLogic(
    val user: User,
    val api: DomainAPIComponent#DomainAPI,
    val rand: RandomComponent#Random) 
    
    extends CalculatingRights
    with ProposingQuests 
    with SolvingQuests
    with VotingQuestProposals
    with VotingQuestSolutions
    with DailyResults
    with Friends
    with MiscUserLogic
    
    with CommonUtil {
}

