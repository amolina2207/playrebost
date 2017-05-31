package controllers

import scala.concurrent.Future
import models.Identity
import models.restResultWrites
import play.api.cache.CacheApi
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Format
import play.api.libs.json.JsArray
import play.api.libs.json.JsError
import play.api.libs.json.JsString
import play.api.libs.json.JsSuccess
import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import play.api.libs.json.Reads
import play.api.mvc.Action
import play.api.mvc.Request
import play.api.mvc.Result
import reactivemongo.play.json.JsObjectDocumentWriter
import services.CRUDService
import utils.RequestUtils

abstract class CRUDController[E: Format, ID](val service: CRUDService[E, ID])(cache: CacheApi)(implicit identity: Identity[E, ID]) extends SecurityController(cache) {

  def read(id: ID) = Action.async { implicit request =>

    val f: Future[Option[Boolean]] = checkAccessAuthorized

   val parameters = RequestUtils.getReqParams

    // Future.successful(Option(true))
    f.flatMap (value => {
      // Hack, All tokens are valid
        //if(value.get){
      if(true){
          service.read(id,parameters.lang).map(_.fold(
            NotFound(Json.toJson(models.RestResult[E](Seq(),Seq(models.Error("0001",s"Entity #$id not found")), models.Status.NOK)))
          )(entity =>
            Ok(Json.toJson(models.RestResult[E](Seq(entity),Seq(), models.Status.OK)))
          ))
        }else{
          Future.successful(Ok(Json.toJson(models.RestResult[E](Seq(),Seq(models.Error("9999",s"The token used is not valid")), models.Status.NOK))))
        }
    })
 }

  def update = Action.async(parse.json) { implicit request =>
    validateAndThen[E] {
      entity =>
        service.update(entity).map {
          case Right(id) => Ok(Json.toJson(models.RestResult[E](Seq(entity),Seq(),models.Status.OK)))
          case Left(err) => BadRequest(Json.toJson(models.RestResult[E](Seq(),Seq(err),models.Status.NOK)))
        }
    }
  }

  def delete(id: ID) = Action.async {
    service.delete(id).map {
      case Right(id) => Ok
      case Left(err) => BadRequest(err)
    }
  }
   
  def create = Action.async(parse.json) { implicit request =>
    validateAndThen[E] {
      entity =>
        service.create(entity).map {
          case Right(ent) => Created(Json.toJson(models.RestResult[E](Seq(ent),Seq(),models.Status.OK)))
          case Left(err) => BadRequest(err)
        }
    }
  }

  def validateAndThen[T: Reads](t: T => Future[Result])(implicit request: Request[JsValue]) = {
    request.body.validate[T].map(t) match {
      case JsSuccess(result, _) =>
        result.recover { case e => BadRequest(e.getMessage()) }
    // Given a valid json IN format, this case only happens when no valid json IN types is provided, for intance String as Int
      case JsError(err) => 
        Future.successful(BadRequest(Json.toJson(err.map {
          case (path, errors) => Json.obj("path" -> path.toString, "errors" -> JsArray(errors.flatMap(e => e.messages.map(JsString(_)))))
        })))
    }
  } 

  def options(path: String) = Action {
    Ok("").withHeaders(
      "Access-Control-Allow-Origin" -> "*",
      "Access-Control-Allow-Methods" -> "GET, POST, PUT, DELETE, OPTIONS",
      "Access-Control-Allow-Headers" -> "Accept, Origin, Content-type, X-Json, X-Prototype-Version, X-Requested-With",
      "Access-Control-Allow-Credentials" -> "true",
      "Access-Control-Max-Age" -> (60 * 60 * 24).toString
    )
  }

}