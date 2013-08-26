package com.github.david04.liftutils.forms.fields.mapper

import com.github.david04.liftutils.forms.fields._
import com.github.david04.liftutils.entity.Entity
import net.liftweb.mapper.{KeyedMapper, MappedText, MappedForeignKey, MappedField}
/**
 * Created by david at 7:48 PM
 */
abstract class MappedForeignKeyFormField[K, E <: Entity[E], O <: KeyedMapper[K, O]](
                                                                                     instance: E,
                                                                                     all: E => Seq[O],
                                                                                     field: E => MappedForeignKey[K, E, O],
                                                                                     fieldName: O => MappedText[O],
                                                                                     placeholder: String = "",
                                                                                     toStr: K => String
                                                                                     )
  extends ForeignKeyFormField[O](
    () => all(instance),
    () => field(instance).toOption,
    o => field(instance).apply(o),
    o => Option(fieldName(o).get).getOrElse(""),
    placeholder,
    o => toStr(o.primaryKeyField.get)
  )
  with MappedFormField[E] {}