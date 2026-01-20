package net.andrewhj.migration.integration.db.flyway.config

final case class FlywayConfig(
    url: String,
    user: String,
    password: String
)
