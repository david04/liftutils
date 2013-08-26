package com.github.david04.liftutils.forms.fields

import com.github.david04.liftutils.forms.{RederedFieldImpl, RederedField, FormField}
import net.liftweb.mapper._
import net.liftweb.http.SHtml
import net.liftweb.http.S
import net.liftweb.util.Helpers._
import net.liftweb.http.js.JsCmds.Run
import com.github.david04.liftutils.entity.Entity
import net.liftweb.http.js.JsCmd
import scala.xml.NodeSeq

abstract class BooleanFormField(
                                 val name: String,
                                 get: () => Boolean,
                                 set: Boolean => Unit,
                                 placeholder: String = "") extends FormField {

  def fieldType = "Bool"

  def render(row: Boolean, edit: Boolean, setTmp: Any => Unit, getTmp: () => Option[Any]) = new RederedFieldImpl[Boolean](setTmp, getTmp) {

    def validate = field.validate

    val html = {
      S.appendJs(Run("$(\".uniform\").uniform();"))
      bind("f", (
        "@wrap [id]" #> id &
          "@name *" #> (name.capitalize) &
          "@help [id]" #> (id + "help")
        )(template(row)),
        "input" -%> SHtml.checkbox(get(), v => set(v), "id" -> (id + "input")))
    }
  }
}



