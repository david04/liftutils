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
import org.joda.time.{DateTimeConstants, Weeks, Days}

sealed trait Recurrence

case class Daily(every: Int) extends Recurrence

case class Weekly(
                   every: Int,
                   sunday: Boolean,
                   monday: Boolean,
                   tuesday: Boolean,
                   wednesday: Boolean,
                   thursday: Boolean,
                   friday: Boolean,
                   saturday: Boolean
                   ) extends Recurrence

case class Monthly(every: Int, dayOfTheMonth: Boolean) extends Recurrence

case class Yearly(every: Int) extends Recurrence

case class Task(
                 recurrence: Recurrence,
                 start: DateTime
                 ) {

  import DateTimeConstants._

  protected def startAt(dayOfTheWeek: Int): DateTime =
    if (start.getDayOfWeek() == dayOfTheWeek) start
    else if (start.getDayOfWeek() < dayOfTheWeek) start.withDayOfWeek(dayOfTheWeek)
    else start.plusWeeks(1).withDayOfWeek(dayOfTheWeek)

  def nextRun(cur: DateTime) = recurrence match {
    case Daily(every) =>
      cur.plusDays(((Days.daysBetween(start, cur).getDays / every) + 1) * every)
    case Weekly(every, sunday, monday, tuesday, wednesday, thursday, friday, saturday) => {
      def iif[T](b: Boolean)(blk: => T): Option[T] = if (b) Some(blk) else None

      iif(sunday)(cur.plusDays(((Days.daysBetween(startAt(SUNDAY), cur).getDays / (every * 7)) + 1) * (every * 7))) ::
        iif(monday)(cur.plusDays(((Days.daysBetween(startAt(MONDAY), cur).getDays / (every * 7)) + 1) * (every * 7))) ::
        iif(tuesday)(cur.plusDays(((Days.daysBetween(startAt(TUESDAY), cur).getDays / (every * 7)) + 1) * (every * 7))) ::
        iif(wednesday)(cur.plusDays(((Days.daysBetween(startAt(WEDNESDAY), cur).getDays / (every * 7)) + 1) * (every * 7))) ::
        iif(thursday)(cur.plusDays(((Days.daysBetween(startAt(THURSDAY), cur).getDays / (every * 7)) + 1) * (every * 7))) ::
        iif(friday)(cur.plusDays(((Days.daysBetween(startAt(FRIDAY), cur).getDays / (every * 7)) + 1) * (every * 7))) ::
        iif(saturday)(cur.plusDays(((Days.daysBetween(startAt(SUNDAY), cur).getDays / (every * 7)) + 1) * (every * 7))) :: Nil
    }.flatten.min
  }

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












