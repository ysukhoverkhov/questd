import play.api._
import components.componentregistry.ComponentRegistrySingleton

object Global extends GlobalSettings {

  override def onStart(app: Application) {

    ComponentRegistrySingleton
    Logger.info(s"Starting Application ${misc.BuildInfo}")
    Logger.trace(s"Trace log is enabled. Starting in development mode.")
  }

}
