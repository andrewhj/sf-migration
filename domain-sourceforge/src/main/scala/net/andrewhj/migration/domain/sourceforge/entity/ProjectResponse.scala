package net.andrewhj.migration.domain.sourceforge.entity

import zio.json.*
import zio.schema.*
import zio.schema.codec.JsonCodec.schemaBasedBinaryCodec

// @jsonMemberNames(SnakeCase)
case class ProjectResponse(
    /* @jsonField("_id") */ id: String,
    status: String,
    `private`: Boolean,
    name: String,
    externalHomepage: String
) // derives JsonCodec

object ProjectResponse:
  given schema: Schema[ProjectResponse] =
    Schema.CaseClass5[
      String,
      String,
      Boolean,
      String,
      String,
      ProjectResponse
    ](
      id0 = TypeId.fromTypeName("ProjectResponse"),
      field01 = Schema.Field(
        name0 = "_id",
        schema0 = Schema[String],
        get0 = _.id,
        set0 = (p, x) => p.copy(id = x)
      ),
      field02 = Schema.Field(
        name0 = "status",
        schema0 = Schema[String],
        get0 = _.status,
        set0 = (p, x) => p.copy(status = x)
      ),
      field03 = Schema.Field(
        name0 = "private",
        schema0 = Schema[Boolean],
        get0 = _.`private`,
        set0 = (p, x) => p.copy(`private` = x)
      ),
      field04 = Schema.Field(
        name0 = "name",
        schema0 = Schema[String],
        get0 = _.name,
        set0 = (p, x) => p.copy(name = x)
      ),
      field05 = Schema.Field(
        name0 = "external_homepage",
        schema0 = Schema[String],
        get0 = _.externalHomepage,
        set0 = (p, x) => p.copy(externalHomepage = x)
      ),
      construct0 = (id, status, isPrivate, name, externalHomepage) =>
        ProjectResponse(id, status, isPrivate, name, externalHomepage)
    )

  given codec: JsonCodec[ProjectResponse] =
    zio.schema.codec.JsonCodec.jsonCodec(schema)
