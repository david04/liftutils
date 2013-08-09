package com.github.david04.liftutils.forms.fields

import com.github.david04.liftutils.forms.{FormField, RederedField}
import net.liftweb.util.Helpers._
import net.liftweb.http.js.JsCmd
import com.github.david04.liftutils.crud.{MetaCrudable, Crudable}
import com.github.david04.liftutils.entity.{Entity, MetaEntity}
import net.liftweb.http.js.JsCmds.RedirectTo

/**
 * Created by david at 5:33 PM
 */

abstract class MappedExtEditTableFormField[E <: Entity[E], O <: Crudable[O]](

                                                                              all: E => E#MappedOneToMany[O],
                                                                              ot: MetaCrudable[O]
                                                                              ) extends FormField[E] {
  def fieldType = "ExtEditTable"

  def render(saveAndRedirect: String => JsCmd, instance: E, row: Boolean, edit: Boolean, setTmp: Any => Unit, getTmp: () => Option[Any]): RederedField = new RederedField {

    def validate = Nil


    def emtpyClass = "table-empty"

    val html = {
      (
        "@title *" #> name &
          "@table" #> ot.table(e => {saveAndRedirect("/app/crud/edit?crud=" + ot.crudId + "&" + e.editParams)}).render &
          "@newline [href]" #> ("/app/crud/create?crud=" + ot.crudId)
        ).apply(template(row))
    }
  }
}
