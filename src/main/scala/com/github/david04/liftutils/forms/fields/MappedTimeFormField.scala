package com.github.david04.liftutils.forms.fields

import com.github.david04.liftutils.forms.{RederedFieldImpl, RederedField, FormField}
import net.liftweb.mapper._
import net.liftweb.http.SHtml
import scala.xml.NodeSeq
import scala.xml.Text
import net.liftweb.http.js.JsCmd
import net.liftweb.util.Helpers._
import com.github.david04.liftutils.entity.Entity

/**
 * Created by david at 5:33 PM
 */
case class MappedTimeFormField[E <: Entity[E]](name: String, field: E => MappedField[Int, E], placeholder: String = "") extends FormField[E] {

  def fieldType = "Time"

  def render( instance: E, row: Boolean, edit: Boolean, setTmp: Any => Unit, getTmp: () => Option[Any]) = new RederedFieldImpl[(String, String)](setTmp, getTmp) {

    value = ("" + field(instance).get / 60, "" + field(instance).get % 60)

    def set() = field(instance).apply(value.get._1.toInt * 60 + value.get._2.toInt)

    def validate = if (!(
      value.get match {
        case (hrValue, minValue) =>
          hrValue.matches("\\d+") && minValue.matches("\\d+") &&
            hrValue.size < 3 && hrValue.toLong < 24 &&
            minValue.size < 3 && minValue.toLong < 60
      })) Text("Invalid time") :: Nil
    else {set(); field(instance).validate.map(_.msg)}

    val html = {
      val hours = SHtml.text(value.get._1, v => value = (v, value.get._2), "id" -> (id + "hr"))
      val min = SHtml.text(value.get._2, v => value = (value.get._1, v), "id" -> (id + "min"))

      (
        "@wrap [id]" #> id &
          "@name *" #> (name.capitalize + ":") &
          "@inputs" #> ((ns: NodeSeq) => bind("time", ns, "hr" -%> hours, "min" -%> min)) &
          "@help [id]" #> (id + "help")
        )(template(row))
    }
  }
}
