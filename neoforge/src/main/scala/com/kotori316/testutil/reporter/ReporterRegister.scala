package com.kotori316.testutil.reporter

import net.minecraft.gametest.framework.{GlobalTestReporter, LogTestReporter}
import net.neoforged.neoforge.event.RegisterGameTestsEvent

import java.nio.file.Path
import java.time.ZonedDateTime
import java.time.format.{DateTimeFormatter, DateTimeFormatterBuilder}
import java.time.temporal.ChronoField
import scala.annotation.unused

object ReporterRegister {

  private val FORMATTER: DateTimeFormatter = DateTimeFormatterBuilder()
    .append(DateTimeFormatter.ISO_LOCAL_DATE)
    .appendLiteral('T')
    .appendValue(ChronoField.HOUR_OF_DAY, 2)
    .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
    .appendValue(ChronoField.SECOND_OF_MINUTE, 2)
    .toFormatter

  def changeReporter(@unused event: RegisterGameTestsEvent): Unit = {
    val time = ZonedDateTime.now().format(FORMATTER)

    GlobalTestReporter.replaceWith(
      CombinedReporter(Seq(LogTestReporter(), CreateFileReporter(Path.of(s"crash-reports/$time"))))
    )
  }
}
