package repositories

import models.Book
import org.scalatest.BeforeAndAfterEach
import org.scalatest.concurrent.PatienceConfiguration
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.Helpers._

class BooksLibraryRepositoryISpec extends PlaySpec
  with GuiceOneServerPerSuite
  with PatienceConfiguration
  with MockitoSugar
  with BeforeAndAfterEach {


  override lazy val app = GuiceApplicationBuilder()
    .configure(
      "mongodb.uri" -> "mongodb://localhost:27017/books_library-test"
    )
    .build()

  def booksRepo: BooksLibraryRepository = app.injector.instanceOf[BooksLibraryRepository]

  override def beforeEach(): Unit = {
    super.beforeEach()
    await(booksRepo.drop)
  }

  trait LocalSetup {
    await(booksRepo.add(testBook))

  }
  val testBook = Book(
    title = "The art of memory",
    author = "Frances Yates",
    publisher = "Random House",
    isbn = "9780415220460",
    year = "1992",
    date_added = "Thu Apr 04 15:25:18 BST 2019")

  val testBookOption = Some(Book(
    title = "The art of memory",
    author = "Frances Yates",
    publisher = "Random House",
    isbn = "9780415220460",
    year = "1992",
    date_added = "Thu Apr 04 15:25:18 BST 2019"))

  val exampleListBooks = List(testBook, testBook)

  "Calling BooksLibraryRepository.findAll" should {
    "return all the books" in new LocalSetup {
      await(booksRepo.add(testBook))

      val result = await(booksRepo.findAll())

      result mustBe exampleListBooks
    }
  }

  "Calling BooksLibraryRepository.findOne" should {
    "find the requested book" in new LocalSetup {
      val result = await(booksRepo.findOne("title", testBook.title))

      result mustBe testBookOption
    }

    "return None if the book does not exist" in {
      val result = await(booksRepo.findOne("title", testBook.title))

      result mustBe None
    }
  }
}

