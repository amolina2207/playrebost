package security.auth

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import play.api.mvc.RequestHeader

trait IdContainer[Id] {

  def startNewSession(userId: Id, timeoutInSeconds: Int): AuthenticityToken

  def remove(token: AuthenticityToken): Unit
  def get(token: AuthenticityToken): Option[Id]

  def prolongTimeout(token: AuthenticityToken, timeoutInSeconds: Int): Unit
  
  def checkAccessAuthorized(implicit request: RequestHeader, context: ExecutionContext, timeoutInSeconds: Int): Option[Boolean]
  
  def extractToken(implicit request: RequestHeader): Option[String]

}
