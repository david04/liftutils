package com.github.david04.liftutils.util

import com.github.david04.liftutils.Loc.Loc

object TimeSince extends Loc {

  val scales = List((1000L, loc("milli")), (60L, loc("second")), (60L, loc("minute")), (24L, loc("hour")), (7L, loc("day")), (10000L, loc("week")))

  def apply(millis: Long): String = {
    def divideInUnits(millis: Long) = scales.foldLeft[(Long, List[(Long, String)])]((millis, Nil)) {
      (total, div) =>
        (total._1 / div._1, (total._1 % div._1, div._2) :: total._2)
    }._2
    def formatAmount(amountUnit: (Long, String)) = amountUnit match {
      case (amount, unit) => amount + " " + unit
    }
    divideInUnits(millis).filter(_._1 > 0).map(formatAmount(_)).mkString(", ")
  }
}