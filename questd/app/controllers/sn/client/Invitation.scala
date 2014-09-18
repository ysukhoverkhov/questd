package controllers.sn.client

/**
 * Invitation to join a game sent and received via Social Network
 *
 * Created by Yury on 18.09.2014.
 */
trait Invitation {

  /**
   * @return Identifier of the invitation in SN.
   */
  def snId: String

  /**
   * Deletes current invitation in SN
   */
  def delete(): Unit
}
