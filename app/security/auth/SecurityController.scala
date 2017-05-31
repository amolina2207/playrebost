package controllers

import java.security.SecureRandom
import play.api.cache._
import play.api.mvc.{Controller, RequestHeader}
import scala.annotation.tailrec
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration.DurationInt
import scala.reflect.{ClassTag, classTag}
import scala.util.Random
import scala.annotation.implicitNotFound
import security.auth.AuthConfig
import security.auth.Role
import security.auth.AuthenticityToken

abstract class SecurityController(cache: CacheApi) extends Controller with AuthConfig {
  
  type Id = String
  type User = models.User
  type Authority = Role
  val idTag: ClassTag[Id] = classTag[Id]
  val sessionTimeoutInSeconds = 3600
  private val tokenSuffix = ":token"
  private val userIdSuffix = ":userId"
  private val random = new Random(new SecureRandom())

  def doLogout(userId: String)(implicit request: RequestHeader, ctx: ExecutionContext) = {
    val tokenId = cache.get[String](userId + userIdSuffix)
    if(tokenId.isDefined){
      cache.remove(tokenId.get + tokenSuffix)
      cache.remove(userId + userIdSuffix)
    }
  }
  
  def doLogin(userId: Id)(implicit request: RequestHeader, ctx: ExecutionContext): Future[String] = {
    startNewSession(userId, sessionTimeoutInSeconds)
  }

  def startNewSession(userId: Id, timeoutInSeconds: Int)(implicit request: RequestHeader, context: ExecutionContext): Future[AuthenticityToken] = {
    /*val oldToken = cache.getOrElse[String](userId.toString + userIdSuffix)("")
    if ( !oldToken.equals("") ) return Future.successful(oldToken)*/
    val token = generate
    store(token, userId, timeoutInSeconds)
    Future.successful(token)
  }
  
  def checkAccessAuthorized(implicit request: RequestHeader, context: ExecutionContext): Future[Option[Boolean]] = {
    (
      for {
        token        <- extractToken
      } yield {
        for {
          usr  <- get(token)
      	  _    <- if (usr.isDefined) prolongTimeout(token, sessionTimeoutInSeconds)
      	          else Future.successful(Some(false))
        } yield {
          if (usr.isDefined) Some(true) else Some(false) 
        }
      }
    ) getOrElse {
      Future.successful(Some(false))
    }
  }
  
  def extractToken(implicit request: RequestHeader): Option[AuthenticityToken] = {
      request.headers.get("token")
  }
  
  @tailrec
  private final def generate: AuthenticityToken = {
    val table = "abcdefghijklmnopqrstuvwxyz1234567890_.~*'()"
    val token = Iterator.continually(random.nextInt(table.size)).map(table).take(64).mkString
    if (syncGet(token).isDefined) generate else token
  }

  def remove(token: AuthenticityToken)(implicit context: ExecutionContext): Future[Unit] = {
    Future.successful(())
  }
  
  private def removeByUserId(userId: Id) {
    cache.get(userId.toString + userIdSuffix).map(_.asInstanceOf[AuthenticityToken]) foreach unsetToken
    unsetUserId(userId)
  }

  private def unsetToken(token: AuthenticityToken) {
    cache.remove(token.toString + tokenSuffix)
  }
  
  private def unsetUserId(userId: Id) {
    cache.remove(userId.toString + userIdSuffix)
  }

  def get(token: AuthenticityToken)(implicit context: ExecutionContext): Future[Option[Id]] = {
    Future.successful(syncGet(token))
  }

  private def syncGet(token: AuthenticityToken): Option[Id] = {
    cache.get[String](token + tokenSuffix)
  }

  private def store(token: AuthenticityToken, userId: Id, timeoutInSeconds: Int) {
    cache.set(token + tokenSuffix, userId, timeoutInSeconds.seconds)
    cache.set(userId + userIdSuffix, token, timeoutInSeconds.seconds)
  }

  def prolongTimeout(token: AuthenticityToken, timeoutInSeconds: Int)(implicit request: RequestHeader, context: ExecutionContext) : Future[Unit] =
    Future.successful(syncGet(token).foreach(store(token, _, timeoutInSeconds)))
}