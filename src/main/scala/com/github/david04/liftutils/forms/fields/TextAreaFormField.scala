package com.github.david04.liftutils.forms.fields

import com.github.david04.liftutils.forms.{FormField, RederedField}
import net.liftweb.http.SHtml
import net.liftweb.util.Helpers._

/**
 * Created by david at 5:33 PM
 */
class TextAreaFormField(
                         val name: String,
                         get: () => String,
                         set: String => Unit,
                         placeholder: String = "") extends FormField {

  def fieldType = "TextArea"

  def render(row: Boolean, edit: Boolean, setTmp: Any => Unit, getTmp: () => Option[Any]) = new RederedField {

    def validate = field.validate

    val html = {
      bind("f", (
        "@wrap [id]" #> id &
          "@name *" #> (name.capitalize) &
          "@help [id]" #> (id + "help")
        )(template(row)),
        "input" -%> SHtml.textarea(get(), set, "id" -> (id + "input"), "placeholder" -> placeholder))
    }
  }
}



