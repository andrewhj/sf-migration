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
