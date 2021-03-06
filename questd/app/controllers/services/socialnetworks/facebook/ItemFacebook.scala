package controllers.services.socialnetworks.facebook

import controllers.services.socialnetworks.client.Item

/**
 * base functionality for all facebook items.
 *
 * Created by Yury on 18.09.2014.
 */
abstract class ItemFacebook extends Item {

  /**
   * Name of social network.
   */
  def snName: String = SocialNetworkClientFacebook.Name

}
