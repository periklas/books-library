package test

import models.Book
import play.api.libs.json.Json

trait BaseSpec {


  val testBook = Book(
    title = "The art of memory",
    author = "Frances Yates",
    publisher = "Random House",
    isbn = "9780415220460",
    year = "1992",
    date_added = "Thu Apr 04 15:25:18 BST 2019")

  val invalidJsonTestBook = Json.parse(
    """{
      | "title": "The art of memory",
      | "author": "Frances Yates",
      | "publisher": "Random House",
      | "year": "1992",
      | "date_added": "Thu Apr 04 15:25:18 BST 2019"
      |}""".stripMargin)

  val testBookJson = Json.parse(
    """{
      | "title": "The art of memory",
      | "author": "Frances Yates",
      | "publisher": "Random House",
      | "isbn": "9780415220460",
      | "year": "1992",
      | "date_added": "Thu Apr 04 15:25:18 BST 2019"
      |}""".stripMargin)


  val exampleListBooks = List(testBook, testBook)
  val exampleListJsonBooks = List(testBookJson, testBookJson)
  val exampleEmptyListBooks = List()
  val exampleEmptyListJsonBooks = Json.parse("""[]""")
}
