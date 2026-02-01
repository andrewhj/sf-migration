package net.andrewhj.migration.integration.github.entity

import zio.json.*
import zio.schema.*
import zio.schema.codec.JsonCodec.schemaBasedBinaryCodec

case class Assignee(id: Int, login: String)

case class Issue(
    id: Long,
    number: Int,
    url: String,
    title: String,
    body: Option[String],
    user: Option[Assignee]
)

case class CreateIssueRequest(
    title: String,
    body: String,
    assignee: Option[String],
    `type`: Option[String],
    assignees: List[String],
    labels: List[String],
    milestone: Option[Int]
)

case class GithubUser(
    id: Long,
    login: String,
    email: Option[String],
    name: Option[String]
)

object CreateIssueRequest:
  given schema: Schema[CreateIssueRequest] = DeriveSchema.gen[CreateIssueRequest]
  given jsonCodec: JsonCodec[CreateIssueRequest] =
    zio.schema.codec.JsonCodec.jsonCodec(schema)

  def apply(title: String, body: String, additionalTags: List[String]): CreateIssueRequest =
    CreateIssueRequest(title, body, None, None, List(), List("sf-migrated"), None)

  def something(title: String, body: String): CreateIssueRequest =
    CreateIssueRequest(
      title,
      body,
      None,
      None,
      List(),
      List("sf-migrated", "sf-plugin-ticket"),
      None
    )

  def bug(title: String, body: String): CreateIssueRequest =
    CreateIssueRequest(title, body, None, Some("bug"), List(), List("sf-migrated", "sf-bug"), None)

  def featureRequest(title: String, body: String): CreateIssueRequest =
    CreateIssueRequest(
      title,
      body,
      None,
      Some("feature-request"),
      List(),
      List("sf-migrated", "sf-feature-request"),
      None
    )

case class Label(id: Long, url: String, name: String, default: Boolean, description: Option[String])

object Label:
  given schema: Schema[Label] = DeriveSchema.gen[Label]

case class CreateIssueResponse(
    id: Long,
    url: String,
    number: Int,
    title: String,
    body: String,
    state: String,
    labels: List[Label],
    assignee: Option[Assignee],
    milestone: Option[Int]
) {
  def toIssue: Issue = Issue(id, number, url, title, Some(body), assignee)
}

object GithubUser:
  given schema: Schema[GithubUser] = DeriveSchema.gen[GithubUser]
  given jsonCodec: JsonCodec[GithubUser] =
    zio.schema.codec.JsonCodec.jsonCodec(schema)

object CreateIssueResponse:
  given schema: Schema[CreateIssueResponse] = DeriveSchema.gen[CreateIssueResponse]
  given jsonCodec: JsonCodec[CreateIssueResponse] =
    zio.schema.codec.JsonCodec.jsonCodec(schema)

object Assignee:
  given schema: Schema[Assignee]       = DeriveSchema.gen[Assignee]
  given jsonCodec: JsonCodec[Assignee] = zio.schema.codec.JsonCodec.jsonCodec(schema)

object Issue:
  given schema: Schema[Issue]       = DeriveSchema.gen[Issue]
  given jsonCodec: JsonCodec[Issue] = zio.schema.codec.JsonCodec.jsonCodec(schema)
