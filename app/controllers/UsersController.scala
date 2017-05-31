package controllers

import models._
import play.api.cache._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._
import play.api.mvc._
import services._

import scala.concurrent.Future

class UsersController(service: UsersService)(cache: CacheApi) extends CRUDController[models.User, String](service)(cache) {

  override def create = Action.async(parse.json) { implicit request =>
    validateAndThen[models.User] {
      entity =>
        service.checkIfUsernameExists(entity.username) flatMap { existsUser => 
          if(existsUser) Future.successful(Ok(Json.toJson(models.RestResult[models.User](Seq(), Seq(models.Error("0003", s"The username already exists")), models.Status.NOK))))
          else super.create(request)
        }
    }
  }
  
  def authenticate = Action.async(parse.json) { implicit request =>
    validateAndThen[models.User] { entity => 
        service.checkUserCredentials(entity) flatMap
        (_.fold(Future.successful(Ok(Json.toJson(models.RestResult[models.User](Seq(), Seq(models.Error("9999", s"The credentials are not correct")), models.Status.NOK)))))
        (user => 
          doLogin(user.username) map { token =>
            user.token = Some(token)
            user.password = None
            service.saveSessionData(models.Session(user.username,user.token))
            Ok(Json.toJson(models.RestResult[models.User](Seq(user), Seq(), models.Status.OK)))
        }))
    }
  }

  def logout = Action.async(parse.json) { implicit request =>
    validateAndThen[models.User] {
      entity => {
        checkAccessAuthorized.map { value =>
          if (value.get) {
            doLogout(entity.username)
            service.removeSessionData(models.Session(entity.username,extractToken))
            Ok(Json.toJson(models.RestResult[models.User](Seq(), Seq(models.Error("0000", s"The Session has been closed")), models.Status.OK)))
          } else {
            Ok(Json.toJson(models.RestResult[models.User](Seq(), Seq(models.Error("9999", s"The token used is not valid")), models.Status.NOK)))
          }
        }

      }
    }
  }
}