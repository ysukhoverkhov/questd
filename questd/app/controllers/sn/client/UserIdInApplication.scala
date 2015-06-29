package controllers.sn.client

/**
 * Another our app user is playing in this social network.
 *
 * Created by Yury on 18.09.2014.
 */
trait UserIdInApplication extends Item {

  /**
   * @return Name of the application.
   */
  def appName: String
}
