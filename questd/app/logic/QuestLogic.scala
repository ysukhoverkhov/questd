package logic

//import java.util.Date
//import scala.util.Random
//import com.github.nscala_time.time.Imports._
//import org.joda.time.DateTime
//import models.domain._
//import models.domain.ContentType._
//import controllers.domain.user.protocol.ProfileModificationResult._
import components.componentregistry.ComponentRegistrySingleton
import play.Logger
import models.domain._

class QuestLogic(val quest: Quest) {

  lazy val api = ComponentRegistrySingleton.api

  /**
   * Check should quest change its status or should not.
   */
  // TODO implement me.
  def updateStatus: Quest = {
    
    // check for adding quest to rotation.
    
    // check for removing quest from rotation.
    
    // check for banning quest.
    
    // check for banning quest by time.
    
    quest
  }

}

