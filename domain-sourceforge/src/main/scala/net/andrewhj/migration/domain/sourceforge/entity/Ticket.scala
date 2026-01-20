package net.andrewhj.migration.domain.sourceforge.entity

import zio.*
import zio.json.*
import zio.schema.*

case class Ticket(summary: String, ticketNum: Int)

object Ticket:
  given schema: Schema[Ticket] = Schema.CaseClass2[String, Int, Ticket](
    id0 = TypeId.fromTypeName("Ticket"),
    field01 = Schema.Field(
      name0 = "summary",
      schema0 = Schema[String],
      get0 = _.summary,
      set0 = (p, x) => p.copy(summary = x)
    ),
    field02 = Schema.Field(
      name0 = "ticket_num",
      schema0 = Schema[Int],
      get0 = _.ticketNum,
      set0 = (p, x) => p.copy(ticketNum = x)
    ),
    construct0 = Ticket.apply
  )

  given codec: JsonCodec[Ticket] = zio.schema.codec.JsonCodec.jsonCodec(schema)

case class Milestone(
    dueDate: String,
    complete: Boolean,
    closed: Int,
    description: String,
    total: Int,
    name: String
)

case class TicketResponsePage(
    tickets: List[Ticket],
    count: Int,
    milestones: List[Milestone],
    page: Int,
    limit: Int
)

object TicketResponsePage:
  given schema: Schema[TicketResponsePage] =
    Schema.CaseClass5[List[Ticket], Int, List[Milestone], Int, Int, TicketResponsePage](
      id0 = TypeId.fromTypeName("TicketResponsePage"),
      field01 = Schema.Field(
        name0 = "tickets",
        schema0 = Schema[List[Ticket]],
        get0 = _.tickets,
        set0 = (p, x) => p.copy(tickets = x)
      ),
      field02 = Schema.Field(
        name0 = "count",
        schema0 = Schema[Int],
        get0 = _.count,
        set0 = (p, x) => p.copy(count = x)
      ),
      field03 = Schema.Field(
        name0 = "milestones",
        schema0 = Schema[List[Milestone]],
        get0 = _.milestones,
        set0 = (p, x) => p.copy(milestones = x)
      ),
      field04 = Schema.Field(
        name0 = "page",
        schema0 = Schema[Int],
        get0 = _.page,
        set0 = (p, x) => p.copy(page = x)
      ),
      field05 = Schema.Field(
        name0 = "limit",
        schema0 = Schema[Int],
        get0 = _.limit,
        set0 = (p, x) => p.copy(limit = x)
      ),
      construct0 = TicketResponsePage.apply
    )

  given codec: JsonCodec[TicketResponsePage] = zio.schema.codec.JsonCodec.jsonCodec(schema)

object Milestone:
  given schema: Schema[Milestone] =
    Schema.CaseClass6[String, Boolean, Int, String, Int, String, Milestone](
      id0 = TypeId.fromTypeName("Milestone"),
      field01 = Schema.Field(
        name0 = "due_date",
        schema0 = Schema[String],
        get0 = _.dueDate,
        set0 = (p, x) => p.copy(dueDate = x)
      ),
      field02 = Schema.Field(
        name0 = "complete",
        schema0 = Schema[Boolean],
        get0 = _.complete,
        set0 = (p, x) => p.copy(complete = x)
      ),
      field03 = Schema.Field(
        name0 = "closed",
        schema0 = Schema[Int],
        get0 = _.closed,
        set0 = (p, x) => p.copy(closed = x)
      ),
      field04 = Schema.Field(
        name0 = "description",
        schema0 = Schema[String],
        get0 = _.description,
        set0 = (p, x) => p.copy(description = x)
      ),
      field05 = Schema.Field(
        name0 = "total",
        schema0 = Schema[Int],
        get0 = _.total,
        set0 = (p, x) => p.copy(total = x)
      ),
      field06 = Schema.Field(
        name0 = "name",
        schema0 = Schema[String],
        get0 = _.name,
        set0 = (p, x) => p.copy(name = x)
      ),
      construct0 = Milestone.apply
    )

  given codec: JsonCodec[Milestone] = zio.schema.codec.JsonCodec.jsonCodec(schema)
