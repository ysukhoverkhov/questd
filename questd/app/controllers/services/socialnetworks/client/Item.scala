package controllers.services.socialnetworks.client

/**
 * Base properties for all social network items.
 *
 * Created by Yury on 18.09.2014.
 */
trait Item {

  /**
   * @return Identifier of the item in SN.
   */
  def snId: String

  /**
   * @return name of our social network.
   */
  def snName: String
}
