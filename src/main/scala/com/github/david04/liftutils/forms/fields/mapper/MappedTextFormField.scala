package com.github.david04.liftutils.forms.fields.mapper

import com.github.david04.liftutils.forms.fields._
import com.github.david04.liftutils.entity.Entity
import net.liftweb.mapper.MappedField
/**
 * Created by david at 7:48 PM
 */
case class MappedTextFormField[E <: Entity[E]](
                                                    instance: E,
                                                    private val _name: String,
                                                    protected val fld: E => MappedField[String, E],
                                                    placeholder: String = "")
  extends TextFormField(_name, () => fld(instance).get, fld(instance).apply _, placeholder)
  with MappedFormField[E] {}
