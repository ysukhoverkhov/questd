package controllers.web.admin.component

import controllers.domain.DomainAPIComponent
import controllers.domain.app.user.BroadcastMessageRequest
import models.domain.user.message.MessageInformation
import play.api.Logger
import play.api.Play.current
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.Messages.Implicits._
import play.api.mvc._

case class MessageForm(
  text: String,
  url: String)

class MessagesImpl (val api: DomainAPIComponent#DomainAPI) extends Controller with SecurityAdminImpl {

  private val form = Form(
    mapping(
      "text" -> nonEmptyText,
      "url" -> text)(MessageForm.apply)(MessageForm.unapply))

  def compose(sendResult: String) = Authenticated { implicit request =>
    Ok(views.html.admin.messages(Menu(request), form, sendResult))
  }

  def send = Authenticated { implicit request =>
    form.bindFromRequest.fold(

      formWithErrors => {

        Logger.error(s"$formWithErrors.errors")

        BadRequest(views.html.admin.messages(
          Menu(request),
          formWithErrors,
          "Form was filled with errors."))
      },

      messageForm => {

        api.broadcastMessage(BroadcastMessageRequest(MessageInformation(
          text = messageForm.text,
          url = Some(messageForm.url))))

        Redirect(controllers.web.admin.routes.Messages.compose("The message has been sent to everyone!"))
      })
  }
}

