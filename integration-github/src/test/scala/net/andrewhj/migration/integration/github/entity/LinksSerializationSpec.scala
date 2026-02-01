package net.andrewhj.migration.integration.github.entity

import zio.*
import zio.test.*
import zio.json.*
import zio.test.Assertion.*

object LinksSerializationSpec extends ZIOSpecDefault {
  val exampleLinkHeader =
    """<https://api.github.com/repositories/1300192/issues?page=2>; rel="prev", <https://api.github.com/repositories/1300192/issues?page=4>; rel="next", <https://api.github.com/repositories/1300192/issues?page=515>; rel="last", <https://api.github.com/repositories/1300192/issues?page=1>; rel="first" """
  val exampleSingleLinkHeader =
    """<https://api.github.com/repositories/1300192/issues?page=4>; rel="next" """

  def spec = suite("LinksSerialization") {
    test("Empty when no links available") {
      val expected = Links.empty
      for {
        actual <- Links.parse("")
      } yield assertTrue(actual == expected)
    }
    test("Serializes single link") {
      val expectedMap =
        Map[String, String](
          "next" -> """https://api.github.com/repositories/1300192/issues?page=4"""
        )

      val expected = Links(expectedMap)
      for {
        actual <- Links.parse(exampleSingleLinkHeader)
      } yield assertTrue(actual == expected)
    }
    test("Serializes full link map") {
      val expectedMap =
        Map[String, String](
          "prev"  -> """https://api.github.com/repositories/1300192/issues?page=2""",
          "next"  -> """https://api.github.com/repositories/1300192/issues?page=4""",
          "last"  -> """https://api.github.com/repositories/1300192/issues?page=515""",
          "first" -> """https://api.github.com/repositories/1300192/issues?page=1"""
        )

      val expected = Links(expectedMap)
      for {
        actual <- Links.parse(exampleLinkHeader)
      } yield assertTrue(actual == expected)
    }
    test("Serializes usable link from values") {
      val expectedMap =
        Map[String, String](
          "next" -> """https://api.github.com/repositories/1300192/issues?page=4"""
        )
      val expected = """https://api.github.com/repositories/1300192/issues?page=4"""
      for {
        links <- Links.parse(exampleSingleLinkHeader)
      } yield assertTrue(links.next == Some(expected))
    }
  }
}
