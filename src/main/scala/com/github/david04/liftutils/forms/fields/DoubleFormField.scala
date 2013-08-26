package com.github.david04.liftutils.forms.fields

import com.github.david04.liftutils.forms.{RederedFieldImpl, RederedField, FormField}
import net.liftweb.mapper._
import net.liftweb.http.SHtml
import scala.xml.Text
import net.liftweb.util.Helpers._
import com.github.david04.liftutils.entity.Entity
import net.liftweb.http.js.JsCmd

/**
 * Created by david at 5:33 PM
 */
class DoubleFormField(
                                  val name: String,
                                  get: () => Double,
                                  set: Double => Unit,
                                  placeholder: String = "") extends FormField {

  def fieldType = "Double"

  def render(row: Boolean, edit: Boolean, setTmp: Any => Unit, getTmp: () => Option[Any]) =
    new RederedFieldImpl[String](setTmp, getTmp) {

      value = "" + get()

      private def _set() = set(value.get.toDouble)

      def validate =
        (if (!(value.get.matches("-?(\\d+\\.\\d*|\\d*\\.\\d+|\\d+)") && value.get.size < 8)) Text("Invalid value") :: Nil
        else {_set(); Nil}) ::: field.validate

      val html = {

        bind("f", (
          "@wrap [id]" #> id &
            "@name *" #> (name.capitalize + ":") &
            "@help [id]" #> (id + "help")
          )(template(row)),
          "input" -%> SHtml.text(value.get, value = _, "id" -> (id + "input")))
      }
    }
}



