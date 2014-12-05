package controllers.sn.client

/**
 * Base properties for all social network items.
 *
 * Created by Yury on 18.09.2014.
 */
trait Item {

  /**
   * @return name of our social network.
   */
  def snName: String

  /**
   * @return Identifier of the invitation in SN.
   */
  def snId: String
}
