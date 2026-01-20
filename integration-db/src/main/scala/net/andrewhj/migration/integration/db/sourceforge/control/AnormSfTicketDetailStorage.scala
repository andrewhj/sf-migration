package net.andrewhj.migration.integration.db.sourceforge.control

import net.andrewhj.migration.domain.sourceforge.entity.{TicketDetails, TicketDetailsWithTag}
import zio.*
import anorm.*
import SqlParser.{int, str}
import io.github.gaelrenoux.tranzactio.{DatabaseOps, DbException}
import io.github.gaelrenoux.tranzactio.anorm.*

class AnormSfTicketDetailStorage private (db: Database) extends SourceforgeTicketDetailStorage {

  private object Queries {
    def create(detail: TicketDetails): ZIO[Connection, DbException, Unit] = tzio { implicit c =>
      SQL(
        """insert into sourceforge_ticket_details(internal_id,ticket_num,created_date,assigned_to,assigned_to_id,summary,
          |status,description,reported_by,reported_by_id) values({id},{num},{created},{assigned},{assigned_id},{summary},{status},{description},{reported_by},{reported_by_id})""".stripMargin
      )
        .on(
          "id"             -> detail.id,
          "num"            -> detail.ticketNum,
          "created"        -> detail.createdDate,
          "assigned"       -> detail.assignee,
          "assigned_id"    -> detail.assigneeId,
          "summary"        -> detail.summary,
          "status"         -> detail.status,
          "description"    -> detail.description,
          "reported_by"    -> detail.reportedBy,
          "reported_by_id" -> detail.reportedById
        )
        .executeInsert()
        .map(_ => ())
    }

    def createTagAssociation(
        internalId: String,
        tag: String
    ): ZIO[Connection, DbException, Option[Long]] =
      tzio { implicit c =>
        SQL("""insert into sourceforge_ticket_tags(internal_id,tag_id)
              |select {internal_id}, tag_id from ticket_tags where tag = {tag_name}
              |""".stripMargin)
          .on("internal_id" -> internalId, "tag_name" -> tag)
          .executeInsert()
      }

    def findAll: ZIO[Connection, DbException, List[TicketDetails]] = tzio { implicit c =>
      SQL"select * from sourceforge_ticket_details"
        .as(
          (str("internal_id") ~ int("ticket_num") ~ str("created_date") ~ str(
            "assigned_to"
          ).? ~ str(
            "assigned_to_id"
          ).? ~ str("summary") ~ str("status") ~ str("description") ~ str("reported_by") ~ str(
            "reported_by_id"
          ).?).map(SqlParser.flatten).*
        )
        .map(TicketDetails.apply)
    }
  }

  override def save(detail: TicketDetails): Task[Unit] = {
    db.transactionOrWiden(Queries.create(detail))

  }

  override def save(detailWithTags: TicketDetailsWithTag): Task[Unit] = for {
    _ <- db.transactionOrWiden(Queries.create(detailWithTags.details))
    res <- db.transactionOrWiden(
      Queries.createTagAssociation(detailWithTags.details.id, detailWithTags.tag)
    )
  } yield
    if (res.exists(_ > 0)) ()
    else throw new Exception(s"Could not find tag (${detailWithTags.tag}) in db")

  override def findAll: Task[List[TicketDetails]] = db.transactionOrWiden(Queries.findAll)
}

object AnormSfTicketDetailStorage:
  def layer = ZLayer {
    ZIO.serviceWith[Database](db => AnormSfTicketDetailStorage(db))
  }
