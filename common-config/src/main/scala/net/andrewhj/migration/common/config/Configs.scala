package net.andrewhj.migration.common.config

import zio.*
import zio.config.*
import zio.config.typesafe.*
import zio.config.magnolia.*
import com.typesafe.config.ConfigFactory

object Configs {
  def makeLayer[C](
      path: String
  )(using desc: DeriveConfig[C], tag: Tag[C]): ZLayer[Any, Throwable, C] = ZLayer {
    val config: Config[C] = deriveConfig[C]

    for {
      typesafeConfig <- ZIO.attempt(ConfigFactory.load().getConfig(path))
      providedConfig <- ConfigProvider.fromTypesafeConfigZIO(typesafeConfig)
      loadedVal      <- providedConfig.load(config)
    } yield loadedVal
  }
}
