package com.github.david04.liftutils.forms.fields

import com.github.david04.liftutils.forms.{FormField, RederedField}
import net.liftweb.mapper._
import net.liftweb.http.SHtml
import net.liftweb.util.Helpers._
import com.github.david04.liftutils.entity.Entity

/**
 * Created by david at 5:33 PM
 */
case class MappedPasswordFormField[E <: Entity[E]](name: String, field: E => MappedField[String, E], placeholder: String = "") extends FormField[E] {

  def fieldType = "Password"

  def render( instance: E, row: Boolean, edit: Boolean, setTmp: Any => Unit, getTmp: () => Option[Any]) = new RederedField {

    def validate = field(instance).validate.map(_.msg)

    val html = {
      bind("f", (
        "@wrap [id]" #> id &
          "@name *" #> (name.capitalize) &
          "@help [id]" #> (id + "help")
        )(template(row)),
        "input" -%> SHtml.password("", field(instance).apply _, "id" -> (id + "input"), "placeholder" -> placeholder))
    }
  }
}
