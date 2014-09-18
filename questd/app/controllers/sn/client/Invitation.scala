package controllers.sn.client

/**
 * Invitation to join a game sent and received via Social Network
 *
 * Created by Yury on 18.09.2014.
 */
trait Invitation {

  // TODO: generalize snName and snId since they are common for all objects in sn.

  /**
   * @return name of our social network.
   */
  def snName: String

  /**
   * @return Identifier of the invitation in SN.
   */
  def snId: String

  /**
   * @return SN Id of user who invited us.
   */
  def inviterSnId: String

  /**
   * Deletes current invitation in SN
   */
  def delete(): Unit
}
