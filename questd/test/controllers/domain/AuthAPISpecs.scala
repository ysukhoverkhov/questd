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

// TODO CLEANUP at some point try to remove mockito library from libs (since play should have it on board)
class AuthAPISpecs  extends Specification
  with DatabaseComponent
  with DomainAPIComponent
  with Mockito {
  
  val db = mock[Database]
  val api =  new DomainAPI

  "Auth API" should {
    
    "Register user with new FB id" in {
      success
    }
    
    
  }
  
}


