package net.andrewhj.migration.integration.github.control

import net.andrewhj.migration.common.config.Configs
import net.andrewhj.migration.integration.github.config.*
import zio.*
import zio.http.Client

object FetchGhTickets extends ZIOAppDefault:
  val program: ZIO[ConfiguredGithubMigration, Throwable, Unit] = for {
    migration <- ZIO.service[ConfiguredGithubMigration]

    strm <- migration
      .fetchIssuesStream()
      .drop(65)
      .take(5)
      .foreach(i => ZIO.logInfo(s"Issue: ${i.number}"))
  } yield ()

  override def run: ZIO[ZIOAppArgs & Scope, Throwable, Unit] = {
    ZIO.logInfo("Github Issues") *>
      program.provide(
        Configs.makeLayer[GithubConfig]("migration.github"),
        Configs.makeLayer[GithubProjectConfig]("migration.github.project"),
        GithubProjectHttpClientLive.layer,
        ConfiguredGithubMigrationLive.layer,
        Client.default,
        GithubMigrationLive.layer
      ) *>
      ZIO.logInfo("Done")
  }
