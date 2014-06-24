//  Copyright (c) 2014 David Miguel Antunes <davidmiguel {at} antunes.net>
//
//  Permission is hereby granted, free of charge, to any person obtaining a copy
//  of this software and associated documentation files (the "Software"), to deal
//  in the Software without restriction, including without limitation the rights
//  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
//  copies of the Software, and to permit persons to whom the Software is
//  furnished to do so, subject to the following conditions:
//
//  The above copyright notice and this permission notice shall be included in
//  all copies or substantial portions of the Software.
//
//  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
//  THE SOFTWARE.

package com.github.david04.liftutils.scheduler

import com.github.nscala_time.time.Imports._
import org.joda.time._
import DateTimeConstants._
import java.util.{TimerTask, Timer}
import scala.annotation.tailrec

sealed trait Ends

case class EndsNever() extends Ends

case class EndsAfterNOccurrences(n: Int) extends Ends

case class EndsOn(date: DateTime) extends Ends

sealed trait Recurrence {

  final def nextRun(start: DateTime, cur: DateTime): Option[DateTime] = _nextRun(start, if (cur.isBefore(start)) start.minusDays(1) else cur)
  def _nextRun(start: DateTime, cur: DateTime): Option[DateTime]

  def ends: Ends
  val every: Int

  def withEnds(ends: Ends): Recurrence

  def hasEnded(start: DateTime, cur: DateTime): Boolean = ends match {
    case EndsNever() => false
    case EndsAfterNOccurrences(n) =>
      (1 to n).toIterator.scanLeft[Option[DateTime]](Some(cur))((cur, _) => cur.flatMap(cur => nextRun(start, cur))).exists(n => n.map(_.isAfter(cur)).getOrElse(true))
    case EndsOn(date) => nextRun(start, cur).map(!_.isAfter(date)).getOrElse(true)
  }
}


trait Task {

  def recurrence: Recurrence
  def start: DateTime

  def lastRun: DateTime
  def lastRun_=(v: DateTime): Unit

  def nextRun(cur: DateTime): Option[DateTime] =
    if (recurrence.hasEnded(start.withTimeAtStartOfDay(), cur.withTimeAtStartOfDay())) None
    else recurrence.nextRun(start.withTimeAtStartOfDay(), cur.withTimeAtStartOfDay())
}

trait Scheduler {

  protected val tasks = collection.mutable.Map[Task, () => Unit]()

  val started = System.currentTimeMillis()
  val SPEEDUP = 5000.0
  //  def getNow() = new DateTime((started + (System.currentTimeMillis() - started) * SPEEDUP).toLong)
  def getNow() = org.joda.time.DateTime.now


  def calcWithSpeedup(ts: Long) = ts - System.currentTimeMillis() + 1 //(((v - started) / SPEEDUP).toLong / 20)

  def scheduleTask(task: Task, run: () => Unit) = synchronized {
    tasks += ((task, run))
    thread.int()
  }

  def cancelTask(task: Task) = synchronized {
    tasks -= task
    thread.int()
  }

  def update() = thread.int()

  def getTasks() = synchronized(tasks.keys.toList)

  def contains(t: Task) = synchronized(tasks.contains(t))

  private def scheduler = this

  protected val thread = new Thread() {

    def int() = scheduler.synchronized(scheduler.notifyAll())

    override def run(): Unit = {
      scheduler.synchronized {
        while (true) {

          val now = getNow()
          val nextRuns = scheduler.synchronized(tasks.flatMap(t => t._1.nextRun(t._1.lastRun).map(next => (t._1, t._2, next))).toList)
          val runNow = nextRuns.filter(!_._3.isAfter(now))

          runNow.sortBy(_._3.getMillis).foreach(task => {
            try {
              println(s"[${now.toString("yyyy-MM-dd HH:mm:ss.SSSZZ")}]: Run task (${task._3.toString("yyyy-MM-dd EE")})")
              task._2()
            } catch {
              case e: Exception =>
                println(e.getClass.getSimpleName + " when running task: " + Option(e.getMessage).getOrElse("<no message>"))
            }
            task._1.lastRun = task._3
          })

          scheduler
            .synchronized(tasks.flatMap(t => t._1.nextRun(t._1.lastRun).map(next => (t._1, t._2, next))).toList)
            .filter(_._3.isAfter(now)).map(_._3.getMillis).reduceOption[Long](math.min) match {
            case Some(nextTime) =>
              println(s"[${now.toString("yyyy-MM-dd HH:mm:ss.SSSZZ")}]: Next task to be run at ${new DateTime(nextTime).toString("yyyy-MM-dd EE")}")
              scheduler.wait(calcWithSpeedup(nextTime))
            case None => scheduler.wait(100)
          }
        }
      }
    }
  }

  def start() = thread.start()
}

case class Daily(every: Int = 1, ends: Ends = EndsNever()) extends Recurrence {

  def _nextRun(_start: DateTime, _cur: DateTime): Option[DateTime] = Some {
    def !!(f: => Int) = if (_cur.isBefore(_start)) 0 else f
    _start.plusDays(!!((Days.daysBetween(_start, _cur).getDays / every) + 1) * every)
  }

  def withEnds(ends: Ends): Recurrence = copy(ends = ends)
}

case class Weekly5(ends: Ends = EndsNever()) extends WeeklyBase {

  val every: Int = 1
  val sunday: Boolean = false
  val monday: Boolean = true
  val tuesday: Boolean = true
  val wednesday: Boolean = true
  val thursday: Boolean = true
  val friday: Boolean = true
  val saturday: Boolean = false

  def withEnds(ends: Ends): Recurrence = copy(ends = ends)
}

case class Weekly3(ends: Ends = EndsNever()) extends WeeklyBase {

  val every: Int = 1
  val sunday: Boolean = false
  val monday: Boolean = true
  val tuesday: Boolean = false
  val wednesday: Boolean = true
  val thursday: Boolean = false
  val friday: Boolean = true
  val saturday: Boolean = false

  def withEnds(ends: Ends): Recurrence = copy(ends = ends)
}

case class Weekly2(ends: Ends = EndsNever()) extends WeeklyBase {

  val every: Int = 1
  val sunday: Boolean = false
  val monday: Boolean = false
  val tuesday: Boolean = true
  val wednesday: Boolean = false
  val thursday: Boolean = true
  val friday: Boolean = false
  val saturday: Boolean = false

  def withEnds(ends: Ends): Recurrence = copy(ends = ends)
}

case class Weekly(
                   every: Int = 1,
                   sunday: Boolean = false,
                   monday: Boolean = false,
                   tuesday: Boolean = false,
                   wednesday: Boolean = false,
                   thursday: Boolean = false,
                   friday: Boolean = false,
                   saturday: Boolean = false,
                   ends: Ends = EndsNever()
                   ) extends WeeklyBase {

  def withEnds(ends: Ends): Recurrence = copy(ends = ends)
}

abstract class WeeklyBase extends Recurrence {

  val every: Int
  def sunday: Boolean
  def monday: Boolean
  def tuesday: Boolean
  def wednesday: Boolean
  def thursday: Boolean
  def friday: Boolean
  def saturday: Boolean
  def ends: Ends

  def _nextRun(_start: DateTime, _cur: DateTime): Option[DateTime] =
    if (sunday || monday || tuesday || wednesday || thursday || friday || saturday) {
      Some({
        def iif[T](b: Boolean)(blk: => T): Option[T] = if (b) Some(blk) else None
        def !!(f: => Int) = if (_cur.isBefore(_start)) 0 else f

        def next(dayOfTheWeek: Int, date: DateTime): DateTime =
          if (date.getDayOfWeek <= dayOfTheWeek) date.withDayOfWeek(dayOfTheWeek)
          else date.plusWeeks(1).withDayOfWeek(dayOfTheWeek)

        def forDay(day: Int, enabled: Boolean) = iif(enabled)({
          val start = next(day, _start)
          def !!(f: => Int) = if (_cur.isBefore(start)) 0 else f
          start.plusDays(!!((Days.daysBetween(start, _cur).getDays / (every * 7)) + 1) * (every * 7))
        })

        forDay(SUNDAY, sunday) ::
          forDay(MONDAY, monday) ::
          forDay(TUESDAY, tuesday) ::
          forDay(WEDNESDAY, wednesday) ::
          forDay(THURSDAY, thursday) ::
          forDay(FRIDAY, friday) ::
          forDay(SUNDAY, saturday) :: Nil
      }.flatten.min)
    } else None
}

case class Monthly(
                    every: Int = 1,
                    atDayOfTheMonth: Boolean = true,
                    ends: Ends = EndsNever()
                    ) extends Recurrence {

  def _nextRun(_start: DateTime, _cur: DateTime): Option[DateTime] = Some {

    if (atDayOfTheMonth) {

      if (Months.monthsBetween(_start.withDayOfMonth(1), _cur.withDayOfMonth(1)).getMonths % every == 0 &&
        _start.getDayOfMonth <= _cur.dayOfMonth().withMaximumValue().getDayOfMonth &&
        _cur.getDayOfMonth < _start.getDayOfMonth)
        _cur.withDayOfMonth(_start.getDayOfMonth)
      else
        Iterator.from(1).map(offset => {
          val month = _start.plusMonths(((Months.monthsBetween(_start.withDayOfMonth(1), _cur.withDayOfMonth(1)).getMonths / every) + offset) * every)
          if (_start.getDayOfMonth <= month.dayOfMonth().withMaximumValue().getDayOfMonth) Some(month.withDayOfMonth(_start.getDayOfMonth))
          else None
        }).flatten.next()

    } else {

      def next(_cur: DateTime) =
        Some(_cur.withDayOfMonth(1)
          .withDayOfWeek(_start.getDayOfWeek)
          .plusWeeks(Weeks.weeksBetween(_start.withDayOfMonth(1).withDayOfWeek(_start.getDayOfWeek), _start).getWeeks))
          .filter(_.getMonthOfYear == _cur.getMonthOfYear)

      if (Months.monthsBetween(_start.withDayOfMonth(1), _cur.withDayOfMonth(1)).getMonths % every == 0 &&
        next(_cur).isDefined &&
        _cur.getDayOfMonth < next(_cur).get.getDayOfMonth)
        _cur.withDayOfMonth(next(_cur).get.getDayOfMonth)
      else
        Iterator.from(1).map({
          offset =>
            next(_start.plusMonths(((Months.monthsBetween(_start.withDayOfMonth(1), _cur.withDayOfMonth(1)).getMonths / every) + offset) * every))
        }).flatten.next()
    }
  }

  def withEnds(ends: Ends): Recurrence = copy(ends = ends)
}

case class Yearly(every: Int = 1, ends: Ends = EndsNever()) extends Recurrence {

  def _nextRun(_start: DateTime, _cur: DateTime): Option[DateTime] = Some {

    if (Years.yearsBetween(_start.withDayOfYear(1), _cur.withDayOfYear(1)).getYears % every == 0 &&
      _cur.getMonthOfYear <= _start.getMonthOfYear &&
      _cur.getDayOfMonth < _start.getDayOfMonth &&
      _start.getDayOfMonth <= _cur.withMonthOfYear(_start.getMonthOfYear).dayOfMonth().withMaximumValue().getDayOfMonth
    )
      _cur.withMonthOfYear(_start.getMonthOfYear).withDayOfMonth(_start.getDayOfMonth)
    else
      Iterator.from(1).map(offset => {
        val year = _start.plusYears(((Years.yearsBetween(_start.withDayOfYear(1), _cur.withDayOfYear(1)).getYears / every) + offset) * every)
        if (_start.getDayOfMonth <= year.withMonthOfYear(_start.getMonthOfYear).dayOfMonth().withMaximumValue().getDayOfMonth)
          Some(year.withMonthOfYear(_start.getMonthOfYear).withDayOfMonth(_start.getDayOfMonth))
        else None
      }).flatten.next()
  }

  def withEnds(ends: Ends): Recurrence = copy(ends = ends)
}

object TaskTest extends App {

  val start = new DateTime(2000, 1, 1, 0, 0)

  def testDaily(task: Task, now: DateTime = start, cnt: Int = 40) {
    println(now.toString() + "\t" + task.nextRun(now).toString())
    if (cnt > 0) testDaily(task, now.plusHours(6), cnt - 1)
  }

  def testWeekly(task: Task, now: DateTime = start, cnt: Int = 40) {
    println(now.toString("yyyy-MM-dd EE HH:mm:ss.SSSZZ") + "\t" + task.nextRun(now).get.toString("yyyy-MM-dd EE HH:mm:ss.SSSZZ"))
    if (cnt > 0) testWeekly(task, now.plusHours(23), cnt - 1)
  }

  def testMonthly(task: Task, now: DateTime = start, cnt: Int = 40) {
    println(now.toString("yyyy-MM-dd EE HH:mm:ss.SSSZZ") + "\t" + task.nextRun(now).get.toString("yyyy-MM-dd EE HH:mm:ss.SSSZZ"))
    if (cnt > 0) testMonthly(task, now.plusDays(19), cnt - 1)
  }

  def testYearly(task: Task, now: DateTime = start, cnt: Int = 40) {
    println(now.toString("yyyy-MM-dd HH:mm:ss.SSSZZ") + "\t" + task.nextRun(now).get.toString("yyyy-MM-dd HH:mm:ss.SSSZZ"))
    if (cnt > 0) testYearly(task, now.plusDays(294), cnt - 1)
  }

  case class DefaultTask(val recurrence: Recurrence, val start: DateTime, var lastRun: DateTime = org.joda.time.DateTime.now) extends Task


  println("Daily (every: 1)")
  testDaily(new DefaultTask(Daily(1), start))

  println("Daily (every: 3)")
  testDaily(new DefaultTask(Daily(3), start))

  println("Weekly - Monday (every: 1)")
  testWeekly(new DefaultTask(Weekly(wednesday = true), start))

  println("Weekly - Monday (every: 2)")
  testWeekly(new DefaultTask(Weekly(every = 2, wednesday = true), start))

  println("Weekly - Tuesday & Thursday (every: 1)")
  testWeekly(new DefaultTask(Weekly(tuesday = true, thursday = true), start))

  println("Weekly - Tuesday & Thursday (every: 2)")
  testWeekly(new DefaultTask(Weekly(every = 2, tuesday = true, thursday = true), start))

  println("Monthly - 15th (every: 1)")
  testMonthly(new DefaultTask(Monthly(atDayOfTheMonth = true), new DateTime(2000, 1, 15, 0, 0)))

  println("Monthly - 1st (every: 1)")
  testMonthly(new DefaultTask(Monthly(atDayOfTheMonth = true), new DateTime(2000, 1, 1, 0, 0)))

  println("Monthly - 15th (every: 2)")
  testMonthly(new DefaultTask(Monthly(every = 2, atDayOfTheMonth = true), new DateTime(2000, 1, 15, 0, 0)))

  println("Monthly - 31th")
  testMonthly(new DefaultTask(Monthly(atDayOfTheMonth = true), new DateTime(2000, 1, 31, 0, 0)))

  println("Monthly - 1st Saturday (every: 1)")
  testMonthly(new DefaultTask(Monthly(atDayOfTheMonth = false), new DateTime(2000, 1, 1, 0, 0)))

  println("Monthly - 2nd Wednesday (every: 1)")
  testMonthly(new DefaultTask(Monthly(atDayOfTheMonth = false), new DateTime(2000, 1, 12, 0, 0)))

  println("Monthly - 5th Monday (every: 1)")
  testMonthly(new DefaultTask(Monthly(atDayOfTheMonth = false), new DateTime(2000, 1, 31, 0, 0)))

  println("Yearly - 1st January (every: 1)")
  testMonthly(new DefaultTask(Yearly(), new DateTime(2000, 1, 1, 0, 0)))

  println("Yearly - 29th February (every: 1)")
  testMonthly(new DefaultTask(Yearly(), new DateTime(2000, 2, 29, 0, 0)))

  println("Yearly - 29th February (every: 3)")
  testMonthly(new DefaultTask(Yearly(every = 3), new DateTime(2000, 2, 29, 0, 0)))
}

/*
import com.github.nscala_time.time.Imports._
import org.joda.time._
import DateTimeConstants._
val f = new java.text.SimpleDateFormat("yyyyMMdd")
Days.daysBetween(new DateTime(f.parse("20140101")), new DateTime(f.parse("20140102"))).getDays


val start = new DateTime(f.parse("20140517"))

def startAt(dayOfTheWeek: Int) = {
  if (start.getDayOfWeek() == dayOfTheWeek) start
  else if (start.getDayOfWeek() < dayOfTheWeek) start.withDayOfWeek(dayOfTheWeek)
  else if (start.getDayOfWeek() > dayOfTheWeek) start.plusWeeks(1).withDayOfWeek(dayOfTheWeek)
}

 */












