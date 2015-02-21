package security

import controllers.routes
import play.api.mvc._

trait Secured {

  import Role._
  import utils.MessageType._

  def username(request: RequestHeader) = request.session.get(Security.username)

  def onUnauthorized(request: RequestHeader): Result =
    Results.Redirect(routes.Application.index())
      .flashing( ErrorMessage -> "You cannot access this resource.")

  def onUnauthorized2: Result =
    Results.Redirect(routes.Application.index())
      .flashing( ErrorMessage -> "You cannot access this resource.")

  def loggedIn(f : => Result): EssentialAction = {
    Security.Authenticated(username, onUnauthorized) { user =>
      Action(f)
    }
  }

  def loggedIn(f: Request[AnyContent] => Result): EssentialAction = {
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
    if (r == Role.Admin) {
      f(request)
    } else {
      onUnauthorized(request)
    }
  }
}
