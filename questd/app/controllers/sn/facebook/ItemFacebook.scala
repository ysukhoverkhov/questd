package controllers.sn.facebook

/**
 * base functionality for all facebook items.
 *
 * Created by Yury on 18.09.2014.
 */
abstract class ItemFacebook {

  /**
   * @inheritdoc
   */
  def snName: String = SocialNetworkClientFacebook.Name

}