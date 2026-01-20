package net.andrewhj.migration.integration.sourceforge.control

import net.andrewhj.migration.ZSTask
import net.andrewhj.migration.domain.sourceforge.entity.{Ticket, TicketDetails}
import zio.*
import zio.stream.ZStream

trait SourceforgeMigration:
  def fetchBugsStream: ZSTask[Ticket]
  def fetchBugDetails: ZSTask[TicketDetails]
  def fetchFeatureRequests: ZSTask[Ticket]
  def fetchFeatureRequestDetails: ZSTask[TicketDetails]
  def fetchPluginTickets: ZSTask[Ticket]
  def fetchPluginTicketDetails: ZSTask[TicketDetails]

class SourceforgeMigrationLive private (dao: ProjectHttpClient) extends SourceforgeMigration:
  override def fetchBugsStream: ZSTask[Ticket] = fetchBucket("bugs")

  override def fetchBugDetails: ZSTask[TicketDetails] = fetchBucketDetails("bugs")

  override def fetchFeatureRequests: ZSTask[Ticket] = fetchBucket(
    "feature-requests"
  )

  override def fetchFeatureRequestDetails: ZSTask[TicketDetails] =
    fetchBucketDetails("feature-requests")

  override def fetchPluginTickets: ZSTask[Ticket] = fetchBucket("plugintickets")

  override def fetchPluginTicketDetails: ZSTask[TicketDetails] =
    fetchBucketDetails("plugintickets")

  private def fetchBucket(bucket: String) = dao.fetchTicketStream("dbunit", bucket)
  private def fetchBucketDetails(bucket: String) =
    fetchBucket(bucket).mapZIO(t => dao.fetchTicketDetails("dbunit", bucket, t.ticketNum))

object SourceforgeMigrationLive:
  def layer = ZLayer {
    for {
      client <- ZIO.service[ProjectHttpClient]
    } yield SourceforgeMigrationLive(client)
  }
