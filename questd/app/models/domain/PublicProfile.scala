package models.domain

/**
 * This can be given to client as is, thus contains only public information.
 */
case class PublicProfile(
  publicProfileVersion: Int = 1,
  level: Int = 18, // Should be 0 here.
  bio: Bio = Bio(),
  vip: Boolean = false,
  debug: String = "")

