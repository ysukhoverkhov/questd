package controllers.sn.client

/**
 * Invitation to join a game sent and received via Social Network
 *
 * Created by Yury on 18.09.2014.
 */
trait Invitation extends Item {

  /**
   * @return SN Id of user who invited us.
   */
  def inviterSnId: String

  /**
   * Deletes current invitation in SN
   */
  def delete(): Unit
}
