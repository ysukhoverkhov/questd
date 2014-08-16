package controllers.sn.client

trait SocialNetworkClient {
  
  def fetchUserByToken(token: String): SNUser
  
}
