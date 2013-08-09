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
abstract class MappedForeignKeyFormField[K, E <: Entity[ E], O <: KeyedMapper[K, O]](
                                                                                        all: E => Seq[O],
                                                                                        field: E => MappedForeignKey[K, E, O],
                                                                                        fieldName: O => MappedText[O],
                                                                                        placeholder: String = "",
                                                                                        toStr: K => String,
                                                                                        fromStr: String => K
                                                                                        ) extends FormField[E] {

  def fieldType = "ForeignKey"

  def render( instance: E, row: Boolean, edit: Boolean, setTmp: Any => Unit, getTmp: () => Option[Any]) = new RederedField() {

    def validate = field(instance).validate.map(_.msg)

    val html = {
      val dlft = Option(field(instance).get).map(toStr)
      S.appendJs(Run(
        (dlft.map(d => "$(\"#" + id + "sel\").val(" + d.encJs + ");").getOrElse("") + "$(\"#" + id + "sel\").chosen();")))

      (
        "@wrap [id]" #> id &
          "@name *" #> (name.capitalize + ":") &
          "@placeholder [data-placeholder]" #> placeholder &
          "@select" #> SHtml.select(
            all(instance).map(o => (toStr(o.primaryKeyField.get), fieldName(o).get)),
            Box(dlft),
            s => if (all(instance).exists(_.primaryKeyField.get == fromStr(s))) field(instance).apply(fromStr(s)),
            "id" -> (id + "sel"), "data-placeholder" -> placeholder) &
          "@help [id]" #> (id + "help")
        )(template(row))
    }
  }
}
