package controllers.web.admin.component

import controllers.domain.DomainAPIComponent
import play.api.Play.current
import play.api.i18n.Messages.Implicits._
import play.api.mvc._

class AdminAppImpl(val api: DomainAPIComponent#DomainAPI) extends Controller with SecurityAdminImpl {

  def index = Authenticated { implicit request =>
    Ok(views.html.admin.index(Menu(request)))
  }

  def login = Action { implicit request =>
    storeAuthInfoInResult(Ok(views.html.admin.index(Menu(request))))
  }
}
