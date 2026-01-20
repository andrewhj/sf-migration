package net.andrewhj.migration.integration.sourceforge.entity

import net.andrewhj.migration.domain.sourceforge.entity.{Milestone, ProjectResponse, Ticket, TicketDetailResponse, TicketDetails, TicketResponsePage}
import zio.*
// import zio.schema.codec.JsonCodec
import zio.json.JsonCodec
import zio.test.*
import zio.test.Assertion.*

import java.io.IOException

object JsonCodecSpec extends ZIOSpecDefault {
  def spec = suite("Json Codecs")(
    suite("ProjectRespose")(
      test("json response") {
        val json = """{
                     |  "_id": "50e9298427184605ef0b0961",
                     |  "status": "active",
                     |  "private": false,
                     |  "name": "dbUnit",
                     |  "external_homepage": "http://dbunit.sourceforge.net/"
                     |}""".stripMargin

        val expected = ProjectResponse(
          "50e9298427184605ef0b0961",
          "active",
          false,
          "dbUnit",
          "http://dbunit.sourceforge.net/"
        )

        for {
          actual <- ZIO.fromEither(JsonCodec[ProjectResponse].decodeJson(json))
        } yield assertTrue(actual == expected)
      }
    ),
    suite("TicketSpec") {
      test("json response") {
        val json = """{
                     | "summary": "Test summary",
                     | "ticket_num": 125
                     |}""".stripMargin
        val expected = Ticket("Test summary", 125)
        for {
          actual <- ZIO.fromEither(JsonCodec[Ticket].decodeJson(json))
        } yield assertTrue(actual == expected)
      }
    },
    suite("Milestone Spec")(
      test("json response") {
        val json = """{
                     |  "due_date": "todo",
                     |  "complete": true,
                     |  "closed": 1,
                     |  "description": "Test Milestone",
                     |  "total": 1,
                     |  "name": "Test"
                     |}""".stripMargin
        val expected = Milestone("todo", true, 1, "Test Milestone", 1, "Test")
        for {
          actual <- ZIO.fromEither(JsonCodec[Milestone].decodeJson(json))
        } yield assertTrue(actual == expected)
      }
    ),
    suite("Ticket response")(test("json response") {
      val json = """{
                   |  "tickets": [
                   |    {
                   |      "ticket_num": 453,
                   |      "summary": "test1"
                   |    },
                   |    {
                   |      "ticket_num": 452,
                   |      "summary": "test2"
                   |    }
                   |  ],
                   |  "count": 2,
                   |  "limit": 100,
                   |  "page": 0,
                   |  "milestones": [
                   |    {
                   |      "name": "v1.0",
                   |      "due_date": "",
                   |      "description": "v1.0",
                   |      "complete": true,
                   |      "default": null,
                   |      "total": 0,
                   |      "closed": 0
                   |    },
                   |    {
                   |      "name": "v1.1",
                   |      "due_date": "",
                   |      "description": "v1.1",
                   |      "complete": true,
                   |      "default": null,
                   |      "total": 0,
                   |      "closed": 0
                   |    }
                   |  ]
                   |}""".stripMargin

      val expectedTickets = List(Ticket("test1", 453), Ticket("test2", 452))

      val expectedMilestones = List(
        Milestone("", true, 0, "v1.0", 0, "v1.0"),
        Milestone("", true, 0, "v1.1", 0, "v1.1")
      )

      val expected = TicketResponsePage(
        expectedTickets,
        2,
        expectedMilestones,
        0,
        100
      )

      for {
        actual <- ZIO.fromEither(JsonCodec[TicketResponsePage].decodeJson(json))
      } yield assertTrue(actual == expected)
    }),
    suite("Ticket Details")(
      test("json response") {
//        val json: String = "{\"ticket\":{\"votes_up\":0,\"votes_down\":0,\"_id\":\"68dea1dce3496d731d997532\",\"mod_date\":\"2025-10-06 14:40:27.106000\",\"labels\":[],\"related_artifacts\":[],\"discussion_thread\":{\"_id\":\"028db56240\",\"discussion_id\":\"50e92a5427184605ef0b290c\",\"subject\":\"\",\"limit\":10,\"page\":null,\"posts\":[{\"slug\":\"44c7\",\"text\":\"Sorry I miss the xml file\",\"subject\":\"#453 Table 'EMPLOYEES' not found in tableMap=org.dbunit.dataset.OrderedTableNameMap\",\"author\":\"paviltard\",\"author_icon_url\":\"https://a.fsdn.com/con/images/sandiego/icons/default-avatar.png\",\"timestamp\":\"2025-10-02 16:03:23.223000\",\"last_edited\":null,\"attachments\":[{\"bytes\":135,\"url\":\"https://sourceforge.net/p/dbunit/bugs/_discuss/thread/028db56240/44c7/attachment/dataset.xml\"}],\"is_meta\":false},{\"slug\":\"b340\",\"text\":\"I missed to create the table :-( but I use databaseTester.setSchema(\\\"DBUNIT\\\");\",\"subject\":\"#453 Table 'EMPLOYEES' not found in tableMap=org.dbunit.dataset.OrderedTableNameMap\",\"author\":\"paviltard\",\"author_icon_url\":\"https://a.fsdn.com/con/images/sandiego/icons/default-avatar.png\",\"timestamp\":\"2025-10-02 17:17:01.334000\",\"last_edited\":null,\"attachments\":[],\"is_meta\":false},{\"slug\":\"b340/4e2b\",\"text\":\"If the cause of the error is the table was did not exist, then dbUnit is behaving correctly and this is not a bug.  \",\"subject\":\"#453 Table 'EMPLOYEES' not found in tableMap=org.dbunit.dataset.OrderedTableNameMap\",\"author\":\"jeffjensen\",\"author_icon_url\":\"https://a.fsdn.com/con/images/sandiego/icons/default-avatar.png\",\"timestamp\":\"2025-10-03 04:47:53.611000\",\"last_edited\":null,\"attachments\":[],\"is_meta\":false},{\"slug\":\"cff9\",\"text\":\"- **status**: open --\\u003E pending\\n- **assigned_to**: Jeff Jensen\\n\",\"subject\":\"#453 Table 'EMPLOYEES' not found in tableMap=org.dbunit.dataset.OrderedTableNameMap\",\"author\":\"jeffjensen\",\"author_icon_url\":\"https://a.fsdn.com/con/images/sandiego/icons/default-avatar.png\",\"timestamp\":\"2025-10-03 04:48:10.945000\",\"last_edited\":null,\"attachments\":[],\"is_meta\":true},{\"slug\":\"094a\",\"text\":\"OK, it’s running. Indeed the Setter setSchema is obsolete, but allows reading only the tables of the \\\"DBUNIT\\\" schema. Otherwise, it reads certain XDB tables such as: XDB$ATTRIBUTE, XDB$IMPORT_NM_INFO.\",\"subject\":\"#453 Table 'EMPLOYEES' not found in tableMap=org.dbunit.dataset.OrderedTableNameMap\",\"author\":\"paviltard\",\"author_icon_url\":\"https://a.fsdn.com/con/images/sandiego/icons/default-avatar.png\",\"timestamp\":\"2025-10-06 09:30:39.335000\",\"last_edited\":null,\"attachments\":[],\"is_meta\":false},{\"slug\":\"094a/bab2\",\"text\":\"Thanks for the update, sounds like correct behavior.\\r\\n\",\"subject\":\"#453 Table 'EMPLOYEES' not found in tableMap=org.dbunit.dataset.OrderedTableNameMap\",\"author\":\"jeffjensen\",\"author_icon_url\":\"https://a.fsdn.com/con/images/sandiego/icons/default-avatar.png\",\"timestamp\":\"2025-10-06 11:36:16.352000\",\"last_edited\":null,\"attachments\":[],\"is_meta\":false},{\"slug\":\"65bd\",\"text\":\"- **status**: pending --\\u003E closed-invalid\\n\",\"subject\":\"#453 Table 'EMPLOYEES' not found in tableMap=org.dbunit.dataset.OrderedTableNameMap\",\"author\":\"jeffjensen\",\"author_icon_url\":\"https://a.fsdn.com/con/images/sandiego/icons/default-avatar.png\",\"timestamp\":\"2025-10-06 11:36:47.748000\",\"last_edited\":null,\"attachments\":[],\"is_meta\":true},{\"slug\":\"c880\",\"text\":\"OK Thank you for your help.\",\"subject\":\"#453 Table 'EMPLOYEES' not found in tableMap=org.dbunit.dataset.OrderedTableNameMap\",\"author\":\"paviltard\",\"author_icon_url\":\"https://a.fsdn.com/con/images/sandiego/icons/default-avatar.png\",\"timestamp\":\"2025-10-06 14:40:25.943000\",\"last_edited\":null,\"attachments\":[],\"is_meta\":false}]},\"discussion_thread_url\":\"https://sourceforge.net/rest/p/dbunit/bugs/_discuss/thread/028db56240/\",\"created_date\":\"2025-10-02 16:01:32.213000\",\"ticket_num\":453,\"summary\":\"Table 'EMPLOYEES' not found in tableMap=org.dbunit.dataset.OrderedTableNameMap\",\"description\":\"test description\",\"reported_by\":\"paviltard\",\"assigned_to\":\"jeffjensen\",\"reported_by_id\":\"68de9d8a3185c4c2ccd199df\",\"assigned_to_id\":\"4d0d1119b9363c7bf9000077\",\"status\":\"closed-invalid\",\"private\":false,\"discussion_disabled\":false,\"attachments\":[{\"bytes\":1920,\"url\":\"https://sourceforge.net/p/dbunit/bugs/453/attachment/OracleDbUnitTest.java\"},{\"bytes\":45881,\"url\":\"https://sourceforge.net/p/dbunit/bugs/453/attachment/logback.txt\"}],\"custom_fields\":{\"_fixed_release\":\"(not fixed)\",\"_milestone\":\"v3.0.x\",\"_priority\":\"3\"}}}"
        val json: String =
          """{
            |  "ticket": {
            |    "votes_up": 0,
            |    "votes_down": 0,
            |    "_id": "68dea1dce3496d731d997532",
            |    "created_date": "2025-10-02 16:01:32.213000",
            |    "ticket_num": 453,
            |    "summary": "test summary",
            |    "description": "test description",
            |    "reported_by": "paviltard",
            |    "assigned_to": "jeffjensen",
            |    "reported_by_id": "68de9d8a3185c4c2ccd199df",
            |    "assigned_to_id": "4d0d1119b9363c7bf9000077",
            |    "status": "closed-invalid",
            |    "private": false,
            |    "discussion_disabled": false,
            |    "custom_fields": {
            |      "_fixed_release": "(not fixed)",
            |      "_milestone": "v3.0.x",
            |      "_priority": "3"
            |    }
            |  }
            |}""".stripMargin

        val expectedTicket = TicketDetails(
          "68dea1dce3496d731d997532",
          453,
          "2025-10-02 16:01:32.213000",
          Some("jeffjensen"),
          Some("4d0d1119b9363c7bf9000077"),
          "test summary",
          "closed-invalid",
          "test description",
          "paviltard",
          Some("68de9d8a3185c4c2ccd199df")
        )
        val expected = TicketDetailResponse(expectedTicket)

        for {
          actual <- ZIO.fromEither(JsonCodec[TicketDetailResponse].decodeJson(json))
        } yield assertTrue(actual == expected)
      }
    )
  )
}
