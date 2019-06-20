package config

import play.api.inject.{Binding, Module}
import play.api.{Configuration, Environment}
import repositories._

class LibraryModule extends Module {

  override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] = {

    Seq(
      bind[BooksLibraryRepository].toSelf.eagerly
    )
  }
}