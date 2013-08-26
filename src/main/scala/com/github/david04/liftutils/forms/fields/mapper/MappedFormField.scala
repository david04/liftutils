package com.github.david04.liftutils.forms.fields.mapper

import com.github.david04.liftutils.forms.FormField
import net.liftweb.mapper.MappedField
import com.github.david04.liftutils.entity.Entity

/**
 * Created by david at 10:31 PM
 */
trait MappedFormField[E <: Entity[E]] {
  self: FormField =>

  val instance: E

  protected val fld: E => MappedField[_, E]

  override def validate = fld(instance).validate.map(_.msg)

}
