package com.github.david04.liftutils.forms.fields

import com.github.david04.liftutils.forms.{FormField, RederedField}
import net.liftweb.mapper._
import net.liftweb.http.SHtml
import net.liftweb.http.js.JsCmd
import net.liftweb.util.Helpers._
import com.github.david04.liftutils.entity.Entity

/**
 * Created by david at 5:33 PM
 */
case class MappedTextFormField[E <: Entity[E]](name: String, field: E => MappedField[String, E], placeholder: String = "") extends FormField[E] {

  def fieldType = "Text"

  def render(saveAndRedirect: String => JsCmd, instance: E, row: Boolean, edit: Boolean, setTmp: Any => Unit, getTmp: () => Option[Any]) = new RederedField {

    def validate = field(instance).validate.map(_.msg)

    val html = {
      bind("f", (
        "@wrap [id]" #> id &
          "@name *" #> (name.capitalize) &
          "@help [id]" #> (id + "help")
        )(template(row)),
        "input" -%> SHtml.text(field(instance).get, field(instance).apply _, "id" -> (id + "input"), "placeholder" -> placeholder))
    }
  }
}
