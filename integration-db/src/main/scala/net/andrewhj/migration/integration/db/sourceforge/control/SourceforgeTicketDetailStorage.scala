package net.andrewhj.migration.integration.db.sourceforge.control

import net.andrewhj.migration.domain.sourceforge.entity.*
import zio.*

trait SourceforgeTicketDetailStorage:
  def save(detail: TicketDetails): Task[Unit]
  def save(detailWithTags: TicketDetailsWithTag): Task[Unit]
  def findAll: Task[List[TicketDetails]]
