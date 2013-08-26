package com.github.david04.liftutils.forms.fields

import com.github.david04.liftutils.forms.{FormField, RederedField}
import net.liftweb.common._
import net.liftweb.http.{S, SHtml}
import net.liftweb.http.js.JsCmds.Run
import net.liftweb.util.Helpers._
import net.liftweb.mapper.MappedEnum
import com.github.david04.liftutils.util.Util.___printable

/**
 * Created by david at 5:33 PM
 */
class EnumFormField[ENUM <: Enumeration](
                                                   val name: String,
                                                   enum: ENUM,
                                                   get: () => ENUM#Value,
                                                   set: ENUM#Value => Unit
                                                   ) extends FormField {

  def fieldType = "Enum"

  def render(row: Boolean, edit: Boolean, setTmp: Any => Unit, getTmp: () => Option[Any]) = new RederedField() {

    def validate = field.validate

    val html = {
      val dlft = Some(get().id + "")
      S.appendJs(Run(
        (dlft.map(d => "$(\"#" + id + "sel\").val(" + d.encJs + ");").getOrElse("")  /*+ "$(\"#" + id + "sel\").chosen();"*/)))

      (
        "@wrap [id]" #> id &
          "@name *" #> (name.capitalize + ":") &
          "@select" #> SHtml.select(
            enum.values.map(v => (v.id + "", v.toString)).toSeq.sortBy(_._1),
            Box(dlft),
            s => set(enum(s.toInt)),
            "id" -> (id + "sel")) &
          "@help [id]" #> (id + "help")
        )(template(row))
    }
  }
}



