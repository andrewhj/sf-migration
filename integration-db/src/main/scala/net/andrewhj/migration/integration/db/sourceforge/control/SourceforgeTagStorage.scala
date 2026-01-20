package net.andrewhj.migration.integration.db.sourceforge.control

import zio.*
import anorm.*
import SqlParser.{int, str}
import io.github.gaelrenoux.tranzactio.{DatabaseOps, DbException}
import io.github.gaelrenoux.tranzactio.anorm.*
import net.andrewhj.migration.domain.sourceforge.entity.TicketDetailsWithTag
import zio.query.*

trait SourceforgeTagStorage:
  def findAll: Task[List[String]]
  def findAllTagsWithIds: Task[Map[String, String]]

class AnormSourceforgeTagStorage private (db: Database) extends SourceforgeTagStorage:
  private object Queries:
    def findAll: ZIO[Connection, DbException, List[String]] = tzio { implicit c =>
      SQL"select tag from ticket_tags"
        .as(str("tag").*)
    }

    def findAllTagsWithIds: ZIO[Connection, DbException, Map[String, String]] = tzio { implicit c =>
      SQL"select id, tag from ticket_tags"
        .as((str("id") ~ str("tag")).map(SqlParser.flatten).*)
        .toMap
    }

  override def findAll: Task[List[String]] = db.transactionOrWiden(Queries.findAll)

  override def findAllTagsWithIds: Task[Map[String, String]] =
    db.transactionOrWiden(Queries.findAllTagsWithIds)

object AnormSourceforgeTagStorage:
  def layer = ZLayer {
    ZIO.serviceWith[Database](db => AnormSourceforgeTagStorage(db))
  }

//trait Foo:
//end Foo
//
//class FooExample(tags: Map[String, String]) extends Foo {
//}
//
//object FooExample {
//  def layer = ZLayer {
//    for {
//      sfts <- ZIO.service[SourceforgeTagStorage]
//      tags <- sfts.findAllTagsWithIds
//    } yield FooExample(tags)
//  }
//}

//class ZQueryExample private (tags: Map[String, String]) extends Foo {
//  def singleResult(request: GetTicketWithTag) = for {
//    allTags <- tagStorage.findAll
//  }
//}

//object ZQueryExample {
//  case class GetTicketWithTag(internalId: String) extends Request[Throwable, TicketDetailsWithTag]
//
//
//  lazy val SfDataSource: DataSource.Batched[Any, GetTicketWithTag] =
//    new DataSource.Batched[Any, GetTicketWithTag] {
//      val identifier: String = "SfDataSource"
//
//
//      val program(requests:Chunk[GetTicketWithTag]) =
//        requests.toList match {
//          case request :: Nil => singleResult(request)
////            val result: Task[TicketDetailsWithTag] = {
////              for {
////                sfts <- ZIO.service[SourceforgeTagStorage]
////                results <- sfts.findAll
////              } yield ()
////            }
//        }
//      override def run(
//          requests: Chunk[GetTicketWithTag]
//      )(implicit trace: Trace): ZIO[Any, Nothing, CompletedRequestMap] = {
//        requests.toList match {
//          case request :: Nil =>
//            val result: Task[TicketDetailsWithTag] = {
//              for {
//
//              }
//            }
//        }
//      }
//    }
//}
