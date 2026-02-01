package net.andrewhj.migration.integration.github.entity

import zio.*
import zio.test.*
import zio.json.*
import zio.test.Assertion.*

object IssueSerializationSpec extends ZIOSpecDefault {
  def readFile(file: String): ZIO[Any, Throwable, String] =
    ZIO.attempt {
      val source = scala.io.Source.fromResource(file)
      try source.mkString
      finally source.close()
    }

  def spec = suite("Github Json Serialization")(
    suite("Issue")(
      test("serializes issue json") {
        val file     = "issue.json"
        val assignee = Assignee(956758, "andrewhj")
        val expected = Issue(3055267610L, 459, "https://api.github.com/repos/andrewhj/dbunit-extension/issues/459", "Update Oracle Drivers to Maven Central Version", None, Some(assignee))

        for {
          text <- readFile("issue.json")
          actual <- ZIO.fromEither(JsonCodec[Issue].decodeJson(text))
        } yield assertTrue(actual == expected)
      }
    )
  )

}
