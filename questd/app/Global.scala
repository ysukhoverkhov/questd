import components.componentregistry.ComponentRegistrySingleton
import controllers.framework.ActorStarter
import play.api._

object Global extends GlobalSettings {

  override def onStart(app: Application) {

    ComponentRegistrySingleton
    new ActorStarter // TODO: move starting of actors to canonical place.

    Logger.info(s"Starting Application ${misc.BuildInfo}")
    Logger.trace(s"Trace log is enabled. Starting in development mode.")
  }
}
