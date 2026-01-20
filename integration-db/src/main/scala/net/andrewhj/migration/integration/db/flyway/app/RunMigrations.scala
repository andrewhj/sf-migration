package net.andrewhj.migration.integration.db.flyway.app

import net.andrewhj.migration.integration.db.flyway.control.{FlywayService, FlywayServiceLive}
import zio.*

object RunMigrations extends ZIOAppDefault {
  private def runMigrations = for {
    flyway <- ZIO.service[FlywayService]
    _ <- flyway.runMigrations.catchSome { case e =>
      ZIO.logError("MIGRATIONS FAILED: " + e) *> flyway.runRepairs *> flyway.runMigrations
    }
  } yield ()

  private def program =
    ZIO.logInfo("Running Migrations") *> runMigrations *> ZIO.logInfo("Migrations Complete")

  override def run: ZIO[Any, Throwable, Unit] = program.provide(
    FlywayServiceLive.configuredLayer
  )
}
