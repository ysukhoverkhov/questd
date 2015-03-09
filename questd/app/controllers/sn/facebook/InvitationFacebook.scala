package controllers.sn.facebook

import controllers.sn.client.Invitation

/**
 * Implementation of Invitations for facebook.
 *
 * Created by Yury on 18.09.2014.
 */
private[sn] class InvitationFacebook(appRequest: com.restfb.types.AppRequest,
                                     client: SocialNetworkClientFacebook,
                                     token: String) extends ItemFacebook with Invitation {

  /**
   * @inheritdoc
   */
  def snId = appRequest.getId

  /**
   * @inheritdoc
   */
  def inviterSnId: String = appRequest.getFrom.getId

  /**
   * @inheritdoc
   */
  def delete(): Unit = {
    client.deleteInvitation(token, this)
  }

}

/**
 * Companion object for InvitationFacebook class
 */
private[sn] object InvitationFacebook {
  def apply(appRequest: com.restfb.types.AppRequest,
            client: SocialNetworkClientFacebook,
            token: String): InvitationFacebook = new InvitationFacebook(appRequest, client, token)
}

