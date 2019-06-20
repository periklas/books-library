package models

import play.api.libs.json._

case class Book(
                 title: String,
                 author: String,
                 publisher: String,
                 isbn: String,
                 year: String,
                 date_added: String)

object Book {
  implicit val formats: OFormat[Book] = Json.format[Book]
}
