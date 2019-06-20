package repositories

import javax.inject.{Inject, Singleton}
import models.Book
import play.api.Logger
import play.api.libs.json.Json
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.commands.WriteResult
import reactivemongo.api.{Cursor, ReadPreference}
import reactivemongo.bson.BSONDocument
import reactivemongo.core.errors.DatabaseException
import reactivemongo.play.json.ImplicitBSONHandlers._
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.{ExecutionContext, Future}


@Singleton
class BooksLibraryRepository @Inject()(mongo: ReactiveMongoApi) (implicit ec: ExecutionContext) {
  private val collectionName: String =  "books_library"


  private def collection: Future[JSONCollection] =
    mongo.database.map(_.collection[JSONCollection](collectionName))

  def findOne(searchBy: String, searchParams: String): Future[Option[Book]] =
    collection.flatMap {
      Logger.logger.info(s"[MongoRepository] - [findOne] : About to make a call to mongo collection.")
      val projection = Some(BSONDocument("_id" -> 0))
      _.find(BSONDocument(searchBy -> searchParams), projection).one[Book] map {
        res =>
          if (res.isEmpty) Logger.logger.error(s"[MongoRepository] - [read] : Query returned no results")
          res
      }
    }

  def findAll(): Future[List[Book]] = {
    val projection = Some(BSONDocument("_id" -> 0))
    Logger.logger.info(s"[MongoRepository] - [findAll] : About to make a call to mongo collection.")
    collection.flatMap(col =>
      col
        .find(Json.obj(), projection)
        .cursor[Book](ReadPreference.Primary)
        .collect[List](-1, Cursor.FailOnError()))
  }

  def add(book: Book): Future[Boolean] = {
    val toAdd: Future[WriteResult] =
      collection
        .flatMap(_.insert(ordered = false)
          .one(book))
    Logger.logger.info(s"[MongoRepository] - [add] : Adding a book in mongo repository.")
    toAdd.map { result: WriteResult =>
      result.writeErrors.foreach(e => Logger.logger.info(s"Successfully inserted with LastError: $e"))
      result.writeErrors.isEmpty
    } recover {
      case e: DatabaseException if e.getMessage().contains("E11000 duplicate key error collection") => false
    }
  }

  def drop: Future[Boolean] =
    for {
      result <- collection.flatMap(_.drop(failIfNotFound = false))
    } yield result
}