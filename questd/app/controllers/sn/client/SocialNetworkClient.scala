package controllers.sn.client

trait SocialNetworkClient {

  def fetchUserByToken(token: String): User

  def fetchFriendsByToken(token: String): List[User]

  /**
   * Fetch all invitations from social network
   * @param token - Auth token for social network.
   * @return list of all invitations presented in SN now.
   */
  def fetchInvitations(token: String): List[Invitation]

  /**
   * Deletes invitation from social network.
   * @param token Token we use for auth.
   * @param invitation Invitation to delete.
   */
  def deleteInvitation(token: String, invitation: Invitation): Unit
}
