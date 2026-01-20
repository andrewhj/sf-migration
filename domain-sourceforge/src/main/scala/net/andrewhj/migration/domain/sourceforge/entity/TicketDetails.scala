package net.andrewhj.migration.domain.sourceforge.entity

import zio.json.*
import zio.schema.*

case class TicketDetailResponse(ticket: TicketDetails) derives JsonCodec

object TicketDetailResponse:
  given schema: Schema[TicketDetailResponse] = DeriveSchema.gen[TicketDetailResponse]
//  given schema: Schema[TicketDetailResponse] =
//    Schema.CaseClass1[Ticket, TicketDetailResponse](
//      id0 = TypeId.fromTypeName("TicketDetails"),
//      field0 = Schema.Field(
//        name0 = "ticket",
//        schema0 = Schema[TicketDetails],
//        get0 = _.ticket,
//        set0 = (p, x) => p.copy(ticket = x)
//      ),
//      defaultConstruct0 = t => TicketDetailResponse(t)
//    )

@jsonMemberNames(SnakeCase)
case class TicketDetails(
    @jsonField("_id")
    id: String,
    ticketNum: Int,
    createdDate: String,
    @jsonField("assigned_to")
    assignee: Option[String],
    @jsonField("assigned_to_id")
    assigneeId: Option[String],
    summary: String,
    status: String,
    description: String,
    reportedBy: String,
    reportedById: Option[String]
)

object TicketDetails:
  given schema: Schema[TicketDetails] =
    Schema.CaseClass10[String, Int, String, Option[String], Option[
      String
    ], String, String, String, String, Option[String], TicketDetails](
      id0 = TypeId.fromTypeName("Ticket"),
      field01 = Schema.Field(
        name0 = "_id",
        schema0 = Schema[String],
        get0 = _.id,
        set0 = (p, x) => p.copy(id = x)
      ),
      field02 = Schema.Field(
        name0 = "ticket_num",
        schema0 = Schema[Int],
        get0 = _.ticketNum,
        set0 = (p, x) => p.copy(ticketNum = x)
      ),
      field03 = Schema.Field(
        name0 = "created_date",
        schema0 = Schema[String],
        get0 = _.createdDate,
        set0 = (p, x) => p.copy(createdDate = x)
      ),
      field04 = Schema.Field(
        name0 = "assigned_to",
        schema0 = Schema[Option[String]],
        get0 = _.assignee,
        set0 = (p, x) => p.copy(assignee = x)
      ),
      field05 = Schema.Field(
        name0 = "assigned_to_id",
        schema0 = Schema[Option[String]],
        get0 = _.assigneeId,
        set0 = (p, x) => p.copy(assigneeId = x)
      ),
      field06 = Schema.Field(
        name0 = "summary",
        schema0 = Schema[String],
        get0 = _.summary,
        set0 = (p, x) => p.copy(summary = x)
      ),
      field07 = Schema.Field(
        name0 = "status",
        schema0 = Schema[String],
        get0 = _.status,
        set0 = (p, x) => p.copy(status = x)
      ),
      field08 = Schema.Field(
        name0 = "description",
        schema0 = Schema[String],
        get0 = _.description,
        set0 = (p, x) => p.copy(description = x)
      ),
      field09 = Schema.Field(
        name0 = "reported_by",
        schema0 = Schema[String],
        get0 = _.reportedBy,
        set0 = (p, x) => p.copy(reportedBy = x)
      ),
      field010 = Schema.Field(
        name0 = "reported_by_id",
        schema0 = Schema[Option[String]],
        get0 = _.reportedById,
        set0 = (p, x) => p.copy(reportedById = x)
      ),
      construct0 = TicketDetails.apply
    )

  given codec: JsonCodec[TicketDetails] = zio.schema.codec.JsonCodec.jsonCodec(schema)

case class TicketDetailsWithTag(details: TicketDetails, tag: String)
