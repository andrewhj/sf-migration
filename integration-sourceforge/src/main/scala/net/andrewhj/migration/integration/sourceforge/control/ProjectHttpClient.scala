package net.andrewhj.migration.integration.sourceforge.control

import net.andrewhj.migration.*

import net.andrewhj.migration.domain.sourceforge.entity.{
  ProjectResponse,
  Ticket,
  TicketDetailResponse,
  TicketDetails,
  TicketResponsePage
}
import zio.*
import zio.json.*
import zio.schema.*
import zio.http.*
import zio.schema.codec.JsonCodec.schemaBasedBinaryCodec
import zio.schema.Schema
import zio.stream.ZStream

trait ProjectHttpClient:
  def fetchProject(project: String): Task[Option[ProjectResponse]]
  def fetchTickets(project: String, ticketName: String): Task[TicketResponsePage]
  def fetchTicketStream(project: String, ticketName: String): ZSTask[Ticket]
  def fetchTicketDetails(
      project: String,
      ticketName: String,
      ticketId: Int
  ): Task[TicketDetails]

  /** Streams all ticket details for a given project and group
    */
  def ticketDetailStream(project: String, ticket: String): ZSTask[TicketDetails]

class ProjectHttpClientLive private (client: Client) extends ProjectHttpClient:
  override def fetchProject(project: String): ZIO[Any, Throwable, Option[ProjectResponse]] =
    ZIO.logInfo(s"Searching for project $project") *>
      ZIO.scoped {
        client
          .request(Request.get(s"https://sourceforge.net/rest/p/$project"))
          .flatMap(_.body.to[Option[ProjectResponse]])
      }

  override def fetchTickets(
      project: String,
      ticketName: String
  ): ZIO[Any, Throwable, TicketResponsePage] =
    ZIO.scoped {
      client
        .request(Request.get(s"https://sourceforge.net/rest/p/$project/$ticketName"))
        .flatMap(_.body.to[TicketResponsePage])
    }

  private def getTicketPage(
      project: String,
      ticketName: String,
      pageSize: Int,
      pageNumber: Int
  ): Task[TicketResponsePage] =
    ZIO.logInfo(s"get /$project/$ticketName pageNumber=$pageNumber, pageSize=$pageSize") *>
      ZIO.scoped {
        client
          .batched(
            Request
              .get(s"https://sourceforge.net/rest/p/$project/$ticketName")
              .addQueryParam("page", pageNumber.toString)
              .addQueryParam("limit", pageSize.toString)
          )
          .flatMap(_.body.to[TicketResponsePage])
      }

  override def fetchTicketStream(
      project: String,
      ticketName: String
  ): ZStream[Any, Throwable, Ticket] = {
    ZStream.paginateChunkZIO(0) { pageNumber =>
      for {
        
        page <- getTicketPage(project, ticketName, 100, pageNumber)
      } yield Chunk.fromIterable(page.tickets) -> (if ((page.page * page.limit) >= page.count)
                                                     None
                                                   else Some(pageNumber + 1))
    }
  }

  override def ticketDetailStream(
      project: String,
      ticket: String
  ): ZStream[Any, Throwable, TicketDetails] =
    fetchTicketStream(project, ticket).mapZIO(t => fetchTicketDetails(project, ticket, t.ticketNum))

  override def fetchTicketDetails(
      project: String,
      ticketName: String,
      ticketId: Int
  ): ZIO[Any, Throwable, TicketDetails] =
    ZIO
      .scoped {
        client
          .batched(
            Request
              .get(s"https://sourceforge.net/rest/p/$project/$ticketName/$ticketId")
          )
          .flatMap(_.body.to[TicketDetailResponse])
      }
      .map(_.ticket)

object ProjectHttpClientLive:
  def layer = ZLayer {
    for {
      client <- ZIO.serviceWith[Client](_.host("https://sourceforge.net/rest"))
    } yield ProjectHttpClientLive(client)
  }
