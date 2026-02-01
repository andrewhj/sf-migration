package net.andrewhj.migration.integration.github.entity

import zio.*

case class Links(links: Map[String, String]) {
  import Links.*

  def first: Option[String] = links.get(RelFirst)
  def next: Option[String]  = links.get(RelNext)
  def prev: Option[String]  = links.get(RelPrev)
  def last: Option[String]  = links.get(RelLast)
}

sealed trait ParseFailure
case class ParseHeaderFailure(header: List[String]) extends ParseFailure
case class ParseLinkFailure(input: String)          extends ParseFailure

object Links {
  private type LinkMap = Map[String, String]
  private type Pair    = (String, String)

  private final val RelFirst = "first"
  private final val RelNext  = "next"
  private final val RelPrev  = "prev"
  private final val RelLast  = "last"

  val empty: Links = Links(Map.empty)

  def parse(headerValue: String): ZIO[Any, ParseFailure, Links] = {
    val rows: List[List[String]] =
      headerValue
        .split(",")
        .toList
        .map(_.split(";").iterator.map(_.trim).toList)

    for {
      pairs <- ZIO.foreach(rows)(parseParts(_).flatMap(normalizePair))
    } yield Links(pairs.toMap)
  }

  private def normalizePair(raw: Pair): ZIO[Any, ParseFailure, Pair] = {
    val (relRaw, urlRaw) = raw
    for {
      rel <- parseRel(relRaw)
      url = stripAngleBrackets(urlRaw)
    } yield (rel, url)
  }

  private def parseParts(parts: List[String]): ZIO[Any, ParseFailure, Pair] =
    ZIO.fromEither {
      parts match {
        case url :: rel :: Nil => Right((rel, url))
        case _                 => Left(ParseHeaderFailure(parts))
      }
    }

  private def parseRel(relPart: String): ZIO[Any, ParseFailure, String] =
    ZIO.fromEither {
      relPart match {
        case s"""rel="$rel"""" => Right(rel)
        case _                 => Left(ParseLinkFailure(relPart))
      }
    }

  private def stripAngleBrackets(url: String): String =
    url.stripPrefix("<").stripSuffix(">")
}
