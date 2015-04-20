package models.domain


import models.domain.base.ID

/**
 * This can be given to client as is, thus contains only public information.
 */
case class PublicProfile(
  publicProfileId: String = ID.generateUUID(), // Not used on server.
  publicProfileVersion: Int = 1,
  level: Int = 10, // Should be 1 here.
  bio: Bio = Bio(),
  vip: Boolean = false)

