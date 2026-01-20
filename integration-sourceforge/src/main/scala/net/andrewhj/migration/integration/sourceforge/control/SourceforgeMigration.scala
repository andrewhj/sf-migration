package net.andrewhj.migration.integration.sourceforge.control

import net.andrewhj.migration.ZSTask
import net.andrewhj.migration.domain.sourceforge.entity.{Ticket, TicketDetails}
import zio.*
import zio.stream.ZStream

enum TicketType(val name: String):
  case Bugs            extends TicketType("bugs")
  case FeatureRequests extends TicketType("feature-requests")
  case PluginTickets   extends TicketType("plugintickets")

trait SourceforgeMigration:
  def fetchBugsStream: ZSTask[Ticket]
  def fetchBugDetails: ZSTask[TicketDetails]
  def fetchFeatureRequests: ZSTask[Ticket]
  def fetchFeatureRequestDetails: ZSTask[TicketDetails]
  def fetchPluginTickets: ZSTask[Ticket]
  def fetchPluginTicketDetails: ZSTask[TicketDetails]

class SourceforgeMigrationLive private (dao: ProjectHttpClient) extends SourceforgeMigration:
  override def fetchBugsStream: ZSTask[Ticket] = fetchSummary(TicketType.Bugs)

  override def fetchBugDetails: ZSTask[TicketDetails] = fetchDetails(TicketType.Bugs)

  override def fetchFeatureRequests: ZSTask[Ticket] = fetchSummary(
    TicketType.FeatureRequests
  )

  override def fetchFeatureRequestDetails: ZSTask[TicketDetails] =
    fetchDetails(TicketType.FeatureRequests)

  override def fetchPluginTickets: ZSTask[Ticket] = fetchSummary(TicketType.PluginTickets)

  override def fetchPluginTicketDetails: ZSTask[TicketDetails] =
    fetchDetails(TicketType.PluginTickets)

  private def fetchSummary(ticketType: TicketType) =
    dao.fetchTicketStream("dbunit", ticketType.name)

  private def fetchDetails(ticketType: TicketType) =
    fetchSummary(ticketType).mapZIO(t =>
      dao.fetchTicketDetails("dbunit", ticketType.name, t.ticketNum)
    )

object SourceforgeMigrationLive:
  def layer = ZLayer {
    for {
      client <- ZIO.service[ProjectHttpClient]
    } yield SourceforgeMigrationLive(client)
  }
