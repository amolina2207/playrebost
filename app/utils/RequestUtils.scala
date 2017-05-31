package utils


import models.Find
import play.api.mvc.RequestHeader
import utils.DefaultValues._


trait RequestUtils {

}

object RequestUtils {

  def getReqParams(implicit request: RequestHeader) : Find = {
    Find(Option(request.queryString.get("search").getOrElse(Seq("")).head),
      Option(request.queryString.get("sort").getOrElse(DEFAULT_SORT).head),
      request.queryString.get("sort_dir").getOrElse(DEFAULT_SORT_DIR).head.toInt,
      request.queryString.get("starting").getOrElse(DEFAULT_STARTING).head.toInt,
      request.queryString.get("limit").getOrElse(DEFAULT_LIMIT).head.toInt,
      request.queryString.get("lang").getOrElse(DEFAULT_LANG).head,
      request.queryString.get("tags").getOrElse(DEFAULT_TAGS).head.split(",")
    )
  }

}
