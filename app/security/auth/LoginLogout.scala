//package security.auth
//
//import play.api.mvc._
//import play.api.mvc.Cookie
//import play.api.libs.Crypto
//import scala.concurrent.{Future, ExecutionContext}
//
//trait Login {
//  self: Controller with AuthConfig =>
//
////  def gotoLoginSucceeded(userId: Id)(implicit request: RequestHeader, ctx: ExecutionContext): Future[Result] = {
////    gotoLoginSucceeded(userId, loginSucceeded(request))
////  }
//
//  def gotoLoginSucceededJson(userId: Id)(implicit request: RequestHeader, ctx: ExecutionContext): Future[String] = {
//    idContainer.startNewSession(userId, sessionTimeoutInSeconds)
//  }
//  
//  
//  def gotoLoginSucceeded(userId: Id, result: => Future[Result])(implicit request: RequestHeader, ctx: ExecutionContext): Future[Result] = for {
//    token <- idContainer.startNewSession(userId, sessionTimeoutInSeconds)
//    r     <- result
//  } yield tokenAccessor.put(token)(r)
//}
//
//trait Logout {
//  self: Controller with AuthConfig =>
//
////  def gotoLogoutSucceeded(implicit request: RequestHeader, ctx: ExecutionContext): Future[Result] = {
////    gotoLogoutSucceeded(logoutSucceeded(request))
////  }
//
//  def gotoLogoutSucceeded(result: => Future[Result])(implicit request: RequestHeader, ctx: ExecutionContext): Future[Result] = {
//    tokenAccessor.extract(request) foreach idContainer.remove
//    result.map(tokenAccessor.delete)
//  }
//}
//
//trait LoginLogout extends Login with Logout {
//  self: Controller with AuthConfig =>
//}