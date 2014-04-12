package logic.userlogic

import org.specs2.mutable._
import org.specs2.runner._
import org.specs2.mock.Mockito
import org.junit.runner._
import logic._
import controllers.domain.app.protocol.ProfileModificationResult._
import models.domain._
import org.joda.time.Hours
import components.APIAccessor
import controllers.domain.DomainAPIComponent
import models.store.DatabaseComponent
import components.random.RandomComponent
import components.RandomAccessor
import controllers.domain.admin.AllThemesRequest
import controllers.domain.admin.AllThemesResult
import controllers.domain.OkApiResult
import models.domain.admin.ConfigSection
import controllers.domain.DomainAPIComponent
import controllers.domain.config._ConfigParams
import com.github.nscala_time.time.Imports.DateTime
import com.github.nscala_time.time.Imports.richDateTime
import logic.LogicBootstrapper

private[userlogic] abstract class BaseUserLogicSpecs extends Specification 
  with Mockito 
  with LogicBootstrapper 
  with APIAccessor
  with RandomAccessor
  
  with RandomComponent
  with DatabaseComponent
  with DomainAPIComponent {
  
  val db = mock[Database]
  val api = mock[DomainAPI]
  val rand = mock[Random]
}
