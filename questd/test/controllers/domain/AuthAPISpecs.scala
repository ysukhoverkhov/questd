package controllers.domain

import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.test._
import play.api.test.Helpers._
import models.store._
import models.domain.user._
import play.Logger
import models.store.mongo._
import org.specs2.mock.Mockito
import controllers.domain._
import models.domain.profile._
import controllers.domain._
import org.specs2.matcher.MatchResult
import org.mockito.InOrder

// TODO CLEANUP at some point try to remove mockito library from libs (since play should have it on board)
class AuthAPISpecs extends Specification
  with DatabaseComponent
  with DomainAPIComponent
  with Mockito {
  
  isolated
  
  val db = mock[Database]
  val api =  new DomainAPI

  "Auth API" should {
    
    "Register user with new FB id" in {
      
      val fbid = "fbid"
      
      db.readUserByFBid(anyString) returns None thenReturns Some(User("", Some(fbid), None, Profile()))
//    db.createUser(newUser)
//    db.readUserByFBid(fbid) returns 
//    db.updateUser(user.replaceSessionID(uuid))

      val rv = api.loginfb(LoginFBParams(fbid))

// TODO CLEANUP - test order of calls here when mockito will be ready.
//      there was one(db).readUserByFBid(fbid) then one(db).createUser(anyString)
      
      there were two(db).readUserByFBid(fbid)
      there was one(db).createUser(any[User])
      there was one(db).updateUser(any[User])
      
      rv must beAnInstanceOf[OkApiResult[LoginFBResult]] 
      rv.body must beSome[LoginFBResult]
    }

    
    "Login existing user with new FB id" in {
      
      val fbid = "fbid"
      
      db.readUserByFBid(anyString) returns Some(User("", Some(fbid), None, Profile()))
//    db.updateUser(user.replaceSessionID(uuid))

      val rv = api.loginfb(LoginFBParams(fbid))

// TODO CLEANUP - test order of calls here when mockito will be ready.
//      there was one(db).readUserByFBid(fbid) then one(db).createUser(anyString)
      
      there was one(db).readUserByFBid(fbid)
      there was one(db).updateUser(any[User])
      
      rv must beAnInstanceOf[OkApiResult[LoginFBResult]] 
      rv.body must beSome[LoginFBResult]
    }
    
  }
  
}


