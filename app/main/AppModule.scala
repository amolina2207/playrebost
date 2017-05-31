package main

import scala.concurrent.Future
import controllers._
import services._
import reactivemongo.api._
import com.typesafe.config.ConfigFactory
import play.api.cache._


trait AppModule extends EhCacheComponents {

  import com.softwaremill.macwire._

  lazy val config = ConfigFactory.load

  lazy val driver = new MongoDriver

  lazy val db: Future[DefaultDB] = {
    import scala.concurrent.ExecutionContext.Implicits.global

    val parsedUri = MongoConnection.parseURI(config getString "mongodb.uri")

    for {
      uri <- Future.fromTry(parsedUri)
      con = driver.connection(uri)
      dn <- Future(uri.db.get)
      db <- con.database(dn)
    } yield db
  }
  
  
  lazy val cache: play.api.cache.CacheApi = defaultCacheApi
  lazy val productsService: ProductsService = wire[ProductsMongoService]
	lazy val productsController = wire[ProductsController]
  lazy val usersService: UsersService = wire[UsersMongoService]
  lazy val usersController = wire[UsersController]
  lazy val providersService : ProvidersService = wire[ProvidersMongoService]
  lazy val providersController = wire[ProvidersController]
  lazy val categoriesService : CategoriesService = wire[CategoriesMongoService]
  lazy val categoriesController = wire[CategoriesController]
  lazy val tagsService : TagsService = wire[TagsMongoService]
  lazy val tagsController = wire[TagsController]
  lazy val ordersService : OrdersService = wire[OrdersMongoService]
  lazy val ordersController = wire[OrdersController]

}
