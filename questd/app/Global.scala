import play.api._
import components.componentregistry.ComponentRegistrySingleton

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    
    // Initializing singleton object
    ComponentRegistrySingleton
    
    Logger.info("Application has started")
  }
  
}
