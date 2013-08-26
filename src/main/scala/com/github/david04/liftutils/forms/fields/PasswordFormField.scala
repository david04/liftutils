package com.github.david04.liftutils.forms.fields

import com.github.david04.liftutils.forms.{FormField, RederedField}
import net.liftweb.mapper._
import net.liftweb.http.SHtml
import net.liftweb.util.Helpers._
import com.github.david04.liftutils.entity.Entity

/**
 * Created by david at 5:33 PM
 */
class PasswordFormField(
                         val name: String,
                         set: String => Unit
                         , placeholder: String = "") extends FormField {

  def fieldType = "Password"

  def render(row: Boolean, edit: Boolean, setTmp: Any => Unit, getTmp: () => Option[Any]) = new RederedField {

    def validate = field.validate

    val html = {
      bind("f", (
        "@wrap [id]" #> id &
          "@name *" #> (name.capitalize) &
          "@help [id]" #> (id + "help")
        )(template(row)),
        "input" -%> SHtml.password("", set, "id" -> (id + "input"), "placeholder" -> placeholder))
    }
  }
}



