package security

import controllers.routes
import play.api.mvc._

trait Secured {

  import Role._
  import utils.MessageType._

  def username(request: RequestHeader) = request.session.get(Security.username)

  def onUnauthorized(request: RequestHeader) =
    Results.Redirect(routes.Application.index())
      .flashing( ErrorMessage -> "Brak uprawnień do zasobu")

  def loggedIn(f : => Result) = {
    Security.Authenticated(username, onUnauthorized) { user =>
      Action(f)
    }
  }

  def loggedIn(f: Request[AnyContent] => Result) = {
    Security.Authenticated(username, onUnauthorized) { user =>
      Action(f)
    }
  }

  def withUser(f: String => Request[AnyContent] => Result) = {
    Security.Authenticated(username, onUnauthorized) { user =>
      Action(f(user))
    }
  }

  def withPermission(test: String)(f: Request[AnyContent] => Result) = withUser { user => implicit request =>
    f(request)
  }

  def withRole(r: Role)(f: Request[AnyContent] => Result) = withUser { user => implicit request =>
    println("USER: " + user)
    if (r == Role.Admin) {
      f(request)
    } else {
      onUnauthorized(request)
    }
  }
}
