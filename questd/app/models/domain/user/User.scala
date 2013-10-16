package models.domain.user

case class User (
    
    private val username: String,
    private val password: String,

    private val profile: Profile    
)
