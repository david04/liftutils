package com.github.david04.liftutils.forms.fields.mapper

import com.github.david04.liftutils.forms.fields._
import com.github.david04.liftutils.entity.Entity
import net.liftweb.mapper.MappedField
import java.util.Date

/**
 * Created by david at 7:48 PM
 */
case class MappedDateFormField[E <: Entity[E]](
                                                instance: E,
                                                private val _name: String,
                                                protected val fld: E => MappedField[Date, E])
  extends DateFormField(_name, () => fld(instance).get, fld(instance).apply _)
  with MappedFormField[E] {}
