package controllers.services.socialnetworks.client

trait SocialNetworkClient {

  /**
   * Checks is the token valid and not expired
   *
   * @param token user's token
   * @return True if valid and false otherwise.
   */
  def isValidUserToken(token: String): Boolean

  /**
   * Fetches user's info from SN with his token.
   *
   * @param token User's token.
   * @return
   */
  def fetchUserByToken(token: String): User

  /**
   * Returns user's friends playing the game with user's token.
   *
   * @param token User's token.
   * @return
   */
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

  /**
   * Fetch user ids in other app.
   * @param token Token we use for auth.
   * @return List of ids in other apps.
   */
  def fetchIdsInOtherApps(token: String): List[UserIdInApplication]
}
