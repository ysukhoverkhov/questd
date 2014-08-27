package controllers.web.admin.component

import controllers.domain.DomainAPIComponent
import play.api.mvc._

class AdminAppImpl(val api: DomainAPIComponent#DomainAPI) extends Controller {

  def index = Action {implicit request => 
    Ok(views.html.admin.index(Menu(request)))
  }
}

