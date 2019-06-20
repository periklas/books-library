package controllers

import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice._
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.test.Helpers._
import play.api.test._
import play.modules.reactivemongo.ReactiveMongoApi
import repositories.BooksLibraryRepository
import test.BaseSpec

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class BooksLibraryControllerSpec  extends PlaySpec with GuiceOneAppPerTest with Injecting with MockitoSugar with BaseSpec{

  val mongo: ReactiveMongoApi = mock[ReactiveMongoApi]
  val booksRepo = mock[BooksLibraryRepository]
  val c: BooksLibraryController = new BooksLibraryController(mongo, stubControllerComponents(), booksRepo)

  "Calling BooksLibraryController.listAll" should {
    "return all the books in the collection" in {

      when(booksRepo.findAll()).thenReturn(Future(exampleListBooks))
      val result: Future[Result] = c.listAll.apply(FakeRequest(GET, "/api/listAll"))

      status(result) mustBe 200
    }

    "return an empty list if the collection is empty" in {
      when(booksRepo.findAll()).thenReturn(Future(exampleEmptyListBooks))
      val result = c.listAll.apply(FakeRequest(GET, "/api/listAll"))
      contentAsJson(result) mustEqual exampleEmptyListJsonBooks

      status(result) mustBe 200
    }

    "return an internal server error if exception" in {
      when(booksRepo.findAll()).thenReturn(Future.failed(new InternalError()))
      val result = c.listAll.apply(FakeRequest(GET, "/api/werw"))

      status(result) mustBe 500
    }
  }

  "Calling BooksLibraryController.findBook" should {
    "return the book that was requested" in {
      when(booksRepo.findOne("title", "The art of memory")).thenReturn(Future(Some(testBook)))

      val result = c.findBook("The art of memory").apply(FakeRequest(GET, "api/find"))

      contentAsJson(result) mustEqual testBookJson

      status(result) mustBe OK
    }

    "return empty if there is no matching book" in {
      when(booksRepo.findOne("title", "not valid")).thenReturn(Future.successful(None))

      val result = c.findBook("not valid").apply(FakeRequest(GET, "api/find"))

      contentAsString(result) must include("not been found in the database")

      status(result) mustBe 404

    }

    "return an internal server error if exception" in {
      when(booksRepo.findOne("title", "not valid")).thenReturn(Future.failed(new InternalError))
      val result = c.listAll.apply(FakeRequest(GET, "api/finds"))

      status(result) mustBe 500
    }
  }

  "Calling BooksLibraryController.addBook" should {

    "return Bad Request if the book has not been added" in {
      val fakeRequest = FakeRequest(method = "POST", uri = "/api/addBook", headers = FakeHeaders(Seq("Content-type" -> "application/json")), body = invalidJsonTestBook)

      when(booksRepo.add(testBook)).thenReturn(Future.successful(true))

      val result: Future[Result] = c.addBook().apply(fakeRequest)
      status(result) mustBe BAD_REQUEST
    }

    "return OK successful book has been added" in {
      val fakeRequest = FakeRequest(method = "POST", uri = "/api/addBook", headers = FakeHeaders(Seq("Content-type" -> "application/json")), body = Json.toJson(testBook))

      when(booksRepo.add(testBook)).thenReturn(Future.successful(true))
      val result: Future[Result] = c.addBook.apply(fakeRequest)

      contentAsString(result) must include("has been saved in the database")

      status(result) mustBe 200
    }
  }

}
