package com.github.david04.liftutils.forms.fields

import com.github.david04.liftutils.forms.{FormField, RederedField}
import net.liftweb.mapper._
import net.liftweb.common._
import net.liftweb.http.{S, SHtml}
import net.liftweb.http.js.JsCmd
import net.liftweb.http.js.JsCmds.Run
import net.liftweb.util.Helpers._
import com.github.david04.liftutils.entity.Entity

/**
 * Created by david at 5:33 PM
 */
abstract class ForeignKeyFormField[O](
                                       all: () => Seq[O],
                                       get: () => Option[O],
                                       set: O => Unit,
                                       fieldName: O => String,
                                       placeholder: String = "",
                                       toStr: O => String
                                       ) extends FormField {

  def fieldType = "ForeignKey"

  def render(row: Boolean, edit: Boolean, setTmp: Any => Unit, getTmp: () => Option[Any]) = new RederedField() {

    def validate = field.validate

    val html = {
      val dlft = get().map(toStr)
      S.appendJs(Run(
        (dlft.map(d => "$(\"#" + id + "sel\").val(" + d.encJs + ");").getOrElse("") + "$(\"#" + id + "sel\").chosen();")))

      (
        "@wrap [id]" #> id &
          "@name *" #> (name.capitalize + ":") &
          "@placeholder [data-placeholder]" #> placeholder &
          "@select" #> SHtml.select(
            all().map(o => (toStr(o), fieldName(o))),
            Box(dlft),
            s => all().find(e => toStr(e) == s).foreach(e => set(e)),
            "id" -> (id + "sel"), "data-placeholder" -> placeholder) &
          "@help [id]" #> (id + "help")
        )(template(row))
    }
  }
}



