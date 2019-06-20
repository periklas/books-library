package controllers

import javax.inject.Inject
import models.Book
import play.api.libs.json.{JsError, JsSuccess, JsValue, Json}
import play.api.mvc._
import play.modules.reactivemongo.ReactiveMongoApi
import repositories.BooksLibraryRepository

import scala.concurrent.{ExecutionContext, Future}

class BooksLibraryController @Inject()(val mongo: ReactiveMongoApi, cc: ControllerComponents,
                                       booksLibraryRepo: BooksLibraryRepository)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def listAll: Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    booksLibraryRepo
      .findAll()
      .map(
        books => Ok(Json.toJson(books))).recover {
      case e => InternalServerError(e.getMessage())
    }(ec)
  }

  def findBook(title: String): Action[AnyContent] = Action.async {
    booksLibraryRepo
      .findOne("title", title)
      .map {
        case Some(response) =>  Ok(Json.toJson(response))
        case None => NotFound(Json.obj("message" -> s"Requested book with title: `$title` has not been found in the database"))
      } recover {
      case e => InternalServerError(e.getMessage())
    }
  }

  def addBook(): Action[JsValue] = Action.async(parse.json) { implicit request: Request[JsValue] =>
    Json.fromJson[Book](request.body) match {
      case JsSuccess(book, _) =>
        booksLibraryRepo.add(book).map {
          case true => Ok(Json.obj("status" -> "OK", "message" -> ("Book '" + book.title + "' has been saved in the database.")))
          case _ => BadRequest(Json.obj("status" -> "error parsing json"))
        }
      case JsError(errors) =>
        Future.successful(BadRequest("There was a problem adding a book from the json provided. " + errors))
    }
  }
}