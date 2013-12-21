package com.github.david04.liftutils.daterangepicker

import net.liftweb.json.JsonAST.{JValue, JObject}
import net.liftweb.http.{SHtml, Templates, S}
import net.liftweb.http.js.JsCmd
import net.liftweb.util._
import net.liftweb.util.Helpers._
import net.liftweb.http.js.JsCmds._
import net.liftweb.json.JsonAST._
import net.liftweb.json.Extraction._
import net.liftweb.json.Printer._
import net.liftweb.json.JsonDSL._
import net.liftweb.http.js.JsCmds._
import net.liftweb.json.JsonAST
import com.github.david04.liftutils.util.Util.__print
import net.liftweb.http.js.JE.JsRaw

import com.fasterxml.jackson.module.scala._
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.david04.liftutils.jacksonxml.{JsonSerializable, JSON}

case class DateRangePicker(
                            onSelection: (Long, Long) => JsCmd,
                            options: Option[Options] = None
                            ) {

  val template = Templates("templates-hidden" :: "daterangepicker-dflt" :: Nil).get

  implicit val formats = net.liftweb.json.DefaultFormats

  val id = S.formFuncName

  def render =
    (
      ".daterangepicker-around [id]" #> id &
        ".daterangepicker-around [style]" #> "display: block;"
      ).apply(template) ++
      <tail>
        {Script(OnLoad(Run("$('#" + id + "').daterangepicker(" +
        JSON.writeValueAsString(options).p("options:\n") + ",\n" +
        "function (start, end) {" +
        "$('#" + id + " .daterangepicker-lbl').html(start.format('MMMM D, YYYY') + ' - ' + end.format('MMMM D, YYYY'));" +
        SHtml.jsonCall(
          JsRaw("[start.toDate().getTime(),end.toDate().getTime()]"),
          (v: JValue) => v match {
            case JArray((JInt(from)) :: (JInt(to)) :: Nil) =>
              onSelection(from.toLong, to.toLong)
          }).toJsCmd +
        "}).data('daterangepicker').notify();") & JsShowId(id)))}
      </tail>
}

case class Range(name: String, value: (Moment, Moment))

case class Locale(
                   applyLabel: Option[String] = None,
                   fromLabel: Option[String] = None,
                   toLabel: Option[String] = None,
                   customRangeLabel: Option[String] = None,
                   daysOfWeek: Option[Array[String]] = None,
                   monthNames: Option[Array[String]] = None,
                   firstDay: Option[Int] = None
                   )

case class Options(
                    opens: Option[String] = None,
                    startDate: Option[Moment] = None,
                    endDate: Option[Moment] = None,
                    minDate: Option[String] = None,
                    maxDate: Option[String] = None,
                    //dateLimit
                    showDropdowns: Option[Boolean] = None,
                    showWeekNumbers: Option[Boolean] = None,
                    timePicker: Option[Boolean] = None,
                    timePickerIncrement: Option[Int] = None,
                    timePicker12Hour: Option[Boolean] = None,
                    ranges: Option[Map[String, (Moment, Moment)]] = None,
                    buttonClasses: Option[Array[String]] = None,
                    applyClass: Option[String] = None,
                    cancelClass: Option[String] = None,
                    format: Option[String] = None,
                    separator: Option[String] = None,
                    locale: Option[Locale] = None
                    )

object Moment {

  object Time extends Enumeration {
    val years = Value("years")
    val months = Value("months")
    val days = Value("days")
    val hours = Value("hours")
    val minutes = Value("minutes")
    val seconds = Value("seconds")
    val milliseconds = Value("milliseconds")
  }

}

case class Moment private(cmd: String) extends JsonSerializable {

  def json = Some(cmd)

  def this(millis: Long) = this(s"moment($millis)")

  def this() = this(s"moment()")

  def subtract(amount: Int, unit: Moment.Time.Value) = Moment(s"$cmd.subtract('$unit', $amount)")

  def startOf(unit: Moment.Time.Value) = Moment(s"$cmd.startOf('$unit')")

  def endOf(unit: Moment.Time.Value) = Moment(s"$cmd.endOf('$unit')")
}