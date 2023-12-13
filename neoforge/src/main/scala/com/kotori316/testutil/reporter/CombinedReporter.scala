package com.kotori316.testutil.reporter

import cats.implicits.catsSemigroupalForMonoid
import cats.{Invariant, Monoid, MonoidK}
import net.minecraft.gametest.framework.{GameTestInfo, TestReporter}

case class CombinedReporter(reporters: Seq[TestReporter]) extends TestReporter {
  override def onTestFailed(pTestInfo: GameTestInfo): Unit =
    reporters.foreach(_.onTestFailed(pTestInfo))

  override def onTestSuccess(pTestInfo: GameTestInfo): Unit =
    reporters.foreach(_.onTestSuccess(pTestInfo))

  override def finish(): Unit =
    reporters.foreach(_.finish())

  def append(reporter: TestReporter): CombinedReporter =
    copy(reporters = reporters :+ reporter)
}

object CombinedReporter {
  val empty: CombinedReporter = CombinedReporter(Seq.empty)
  implicit final val monoidReporter: Monoid[CombinedReporter] =
    Invariant[Monoid].imap(MonoidK[Seq].algebra[TestReporter])(CombinedReporter.apply)(_.reporters)
}
