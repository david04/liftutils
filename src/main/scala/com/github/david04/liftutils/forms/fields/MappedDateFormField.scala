package com.github.david04.liftutils.forms.fields

import com.github.david04.liftutils.forms.{RederedFieldImpl, FormField, RederedField}
import net.liftweb.mapper._
import net.liftweb.http.SHtml
import net.liftweb.http.S
import net.liftweb.util.Helpers._
import net.liftweb.http.js.JsCmds.{OnLoad, Run}
import java.util.Date
import scala.xml.Text
import java.text.SimpleDateFormat
import com.github.david04.liftutils.entity.Entity
import net.liftweb.http.js.JsCmd
import com.github.david04.liftutils.crud.Crudable

/**
 * Created by david at 5:33 PM
 */
case class MappedDateFormField[E <: Entity[E]](name: String, field: E => MappedField[Date, E]) extends FormField[E] {

  def fieldType = "Date"

  def render( instance: E, row: Boolean, edit: Boolean, setTmp: Any => Unit, getTmp: () => Option[Any]) = new RederedFieldImpl[String](setTmp, getTmp) {

    def dpFormat = "yyyy-mm-dd"

    def simpleDateFormat = "yyyy-MM-dd"

    val format = new SimpleDateFormat(simpleDateFormat)

    value = Option(field(instance).get).map(format.format _).getOrElse(null)

    def set() { field(instance).set(format.parse(value.get)) }

    def validate = try {format.parse(value.get); Nil} catch {case _: Exception => Text("Invalid date") :: Nil}

    val html = {
      S.appendJs(OnLoad(Run("$('#" + id + "input').datepicker({format: " + dpFormat.encJs + "})" +
        (if (value == null) ".datepicker('setValue', '0');" else ""))))

      bind("f", (
        "@wrap [id]" #> id &
          "@name *" #> (name.capitalize + ":") &
          "@help [id]" #> (id + "help")
        )(template(row)),
        "input" -%> SHtml.text(value.get, value = _, "id" -> (id + "input")))
    }
  }
}
