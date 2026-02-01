package net.andrewhj.migration.integration.github.control

import zio.*
import zio.json.*
import zio.stream.*
import zio.schema.*
import zio.http.*
import zio.schema.codec.JsonCodec.schemaBasedBinaryCodec
import zio.schema.Schema
import net.andrewhj.migration.*
import net.andrewhj.migration.common.config.Configs
import net.andrewhj.migration.integration.github.config.*
import net.andrewhj.migration.integration.github.entity.*

trait ConfiguredGithubMigration:
  def fetchAvailableAssigneesStream(): ZSTask[Assignee]
  def fetchIssuesStream(): ZSTask[Issue]
  def createIssue(issue: CreateIssueRequest): Task[CreateIssueResponse]

class ConfiguredGithubMigrationLive private (
    owner: String,
    project: String,
    migration: GithubMigration
) extends ConfiguredGithubMigration:
  override def fetchAvailableAssigneesStream(): ZSTask[Assignee] =
    migration.fetchAvailableAssigneesStream(owner, project)

  override def fetchIssuesStream(): ZSTask[Issue] =
    migration.fetchIssuesStream(owner, project)

  override def createIssue(issue: CreateIssueRequest): Task[CreateIssueResponse] =
    migration.createIssue(owner, project, issue)

object ConfiguredGithubMigrationLive:
  val layer = ZLayer {
    for {
      config    <- ZIO.service[GithubProjectConfig]
      migration <- ZIO.service[GithubMigration]
    } yield ConfiguredGithubMigrationLive(config.owner, config.project, migration)
  }

  val configuredLayer = Configs.makeLayer[GithubProjectConfig]("migration.github.project") >>> layer

trait GithubMigration:
  def fetchAvailableAssigneesStream(owner: String, project: String): ZSTask[Assignee]
  def fetchIssuesStream(owner: String, project: String): ZSTask[Issue]
  def createIssue(
      owner: String,
      project: String,
      issue: CreateIssueRequest
  ): Task[CreateIssueResponse]

object GithubApi:
  val BaseUrl = "https://api.github.com"

  val LinkHeader = "link"

  val AcceptHeaderName  = "Accept"
  val AcceptHeaderValue = "application/vnd.github+json"

  val ApiVersionHeaderName  = "X-GitHub-Api-Version"
  val ApiVersionHeaderValue = "2022-11-28"

  val AuthorizationHeaderName = "Authorization"

class GithubMigrationLive private (httpClient: GithubProjectHttpClient) extends GithubMigration:
  override def fetchAvailableAssigneesStream(owner: String, project: String): ZSTask[Assignee] =
    val url = s"${GithubApi.BaseUrl}/repos/$owner/$project/assignees"
    paginateGithubList(firstPageUrl = url, fetchTask = httpClient.fetchAssigneesPage)

  override def fetchIssuesStream(owner: String, project: String): ZSTask[Issue] =
    val url = s"${GithubApi.BaseUrl}/repos/$owner/$project/issues"
    paginateGithubList(firstPageUrl = url, fetchTask = httpClient.fetchIssuesPage)

  private def paginateGithubList[A](
      firstPageUrl: String,
      fetchTask: String => Task[PaginatedResponse[A]]
  ): ZSTask[A] =
    ZStream.paginateChunkZIO(firstPageUrl) { pageUrl =>
      for page <- fetchTask(pageUrl)
      yield Chunk.fromIterable(page.items) -> page.links.next
    }

  override def createIssue(
      owner: String,
      project: String,
      issue: CreateIssueRequest
  ): Task[CreateIssueResponse] =
    httpClient.createIssue(owner, project, issue)

object GithubMigrationLive:
  val layer = ZLayer {
    for {
      client <- ZIO.service[GithubProjectHttpClient]
    } yield GithubMigrationLive(client)
  }

trait GithubProjectHttpClient:
  def fetchAvailableAssignees(owner: String, project: String): Task[PaginatedResponse[Assignee]] =
    fetchAssigneesPage(s"https://api.github.com/repos/$owner/$project/assignees")

  def fetchAssigneesPage(link: String): Task[PaginatedResponse[Assignee]]

  def fetchIssues(owner: String, project: String): Task[PaginatedResponse[Issue]] =
    fetchIssuesPage(s"https://api.github.com/repos/$owner/$project/issues")

  def fetchIssuesPage(link: String): Task[PaginatedResponse[Issue]]

  def createIssue(
      owner: String,
      project: String,
      issue: CreateIssueRequest
  ): Task[CreateIssueResponse]

  def fetchUser(username: String): Task[GithubUser]

class GithubProjectHttpClientLive private (config: GithubConfig, client: Client)
    extends GithubProjectHttpClient:
  private lazy val key               = config.token
  private lazy val bearerTokenHeader = s"Bearer $key"

  private val LinkHeaderName = "link"

  override def fetchAssigneesPage(link: String): Task[PaginatedResponse[Assignee]] =
    fetchPage(
      link = link,
      decode = _.body.to[List[Assignee]]
    )

  def fetchIssuesPage(link: String): Task[PaginatedResponse[Issue]] =
    fetchPage(
      link = link,
      decode = _.body.to[List[Issue]]
    )

  private def fetchPage[A](
      link: String,
      decode: Response => Task[List[A]]
  ): Task[PaginatedResponse[A]] =
    for
      _       <- ZIO.logInfo(s"fetching $link")
      resp    <- client.batched(Request.get(link))
      _       <- ZIO.logTrace(s"response: $resp")
      links   <- parseLinkHeader(resp.headers)
      decoded <- decode(resp)
      _       <- ZIO.logTrace(s"decoded: $decoded")
      pr = PaginatedResponse(links, decoded)
    yield pr

  private def parseLinkHeader(headers: Headers): UIO[Links] =
    ZIO
      .fromOption(headers.get(LinkHeaderName))
      .flatMap(lh => Links.parse(lh))
      .orElseSucceed(Links.empty)

  override def createIssue(
      owner: String,
      project: String,
      issue: CreateIssueRequest
  ): Task[CreateIssueResponse] = {
    ZIO.logInfo(s"Creating issue $issue") *>
      ZIO.scoped {
        for {
          body <- ZIO.attempt(Body.fromString(issue.toJson))
          _    <- ZIO.logTrace(s"request body: $body")
          request = Request
            .post(s"https://api.github.com/repos/$owner/$project/issues", body)
            .addHeader(GithubApi.AcceptHeaderName, GithubApi.AcceptHeaderValue)
            .addHeader(GithubApi.ApiVersionHeaderName, GithubApi.ApiVersionHeaderValue)
            .addHeader(GithubApi.AuthorizationHeaderName, bearerTokenHeader)
          response <- client.batched(request)
          _        <- ZIO.logInfo(s"resp: $response")
          issueResponse <- ZIO.flatten(response.status match {
            case Status.Created => ZIO.succeed(response.body.to[CreateIssueResponse])
            case _ =>
              ZIO.logError(s"body: ${response.body.asString}") *>
                ZIO.fail(new RuntimeException("Unexpected status"))
          })
        } yield issueResponse
      }
  }

  override def fetchUser(username: String): Task[GithubUser] =
    ZIO.logInfo(s"Fetch username=$username") *>
      ZIO.scoped {
        for
          link    <- ZIO.succeed(s"https://api.github.com/users/$username")
          resp    <- client.batched(Request.get(link))
          _       <- ZIO.logTrace(s"response $resp")
          decoded <- resp.body.to[GithubUser]
        yield decoded
      }

object GithubProjectHttpClientLive:
  val layer = ZLayer {
    for {
      config <- ZIO.service[GithubConfig]
      client <- ZIO.serviceWith[Client](_.host("https://api.github.com/repos/"))
    } yield GithubProjectHttpClientLive(config, client)
  }

case class PaginatedResponse[A](links: Links, items: List[A])
