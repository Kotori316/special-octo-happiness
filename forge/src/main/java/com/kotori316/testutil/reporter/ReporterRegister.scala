package com.kotori316.testutil.reporter

import com.kotori316.testutil.TestUtilMod
import net.minecraft.gametest.framework.{GlobalTestReporter, LogTestReporter}
import net.minecraftforge.event.RegisterGameTestsEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

import java.nio.file.Path
import java.time.ZonedDateTime
import java.time.format.{DateTimeFormatter, DateTimeFormatterBuilder}
import java.time.temporal.ChronoField
import scala.annotation.unused

@Mod.EventBusSubscriber(modid = TestUtilMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
object ReporterRegister {

  private val FORMATTER: DateTimeFormatter = DateTimeFormatterBuilder()
    .append(DateTimeFormatter.ISO_LOCAL_DATE)
    .appendLiteral('T')
    .appendValue(ChronoField.HOUR_OF_DAY, 2)
    .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
    .appendValue(ChronoField.SECOND_OF_MINUTE, 2)
    .toFormatter

  @SubscribeEvent
  def changeReporter(@unused event: RegisterGameTestsEvent): Unit = {
    val time = ZonedDateTime.now().format(FORMATTER)

    GlobalTestReporter.replaceWith(
      CombinedReporter(Seq(LogTestReporter(), CreateFileReporter(Path.of(s"crash-reports/$time"))))
    )
  }
}
