package net.andrewhj.migration.integration.sourceforge.control

import zio.*
import zio.http.Client

object FetchSfTickets extends ZIOAppDefault:
  val program = for {
    service <- ZIO.service[SourceforgeMigration]
    _       <- service.fetchBugDetails.take(105).foreach(b => ZIO.log(s"Bug: ${b.ticketNum}"))
  } yield ()

  override def run: ZIO[ZIOAppArgs & Scope, Throwable, Unit] = ZIO.logInfo("Sourceforge Bugs") *>
    program.provide(
      ProjectHttpClientLive.layer,
      SourceforgeMigrationLive.layer,
      Client.default
    ) *> ZIO.logInfo("Done")
