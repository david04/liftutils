package com.github.david04.liftutils.entity

import com.github.david04.liftutils.crud.{MetaCrudable, Editable, Crudable}
import com.github.david04.liftutils.forms.fields.{MappedExtEditTableFormField, MappedTableFormField, MappedForeignKeyFormField}
import net.liftweb.mapper._
import net.liftweb.util.FieldError
import scala.xml.Text

trait Entity[T <: Entity[T]] extends LongKeyedMapper[T] with OneToMany[Long, T] with IdPK {
  self: T =>
}

trait MetaEntity[T <: Entity[T]] extends Entity[T] with LongKeyedMetaMapper[T] {
  self: T =>
}

trait ChildEntity[P <: Entity[P], T <: Entity[T]] extends Entity[T] {
  self: T =>

  def pt: KeyedMetaMapper[Long, P]

  def all(acc: P): P#MappedOneToMany[T]

  object parent extends MappedLongForeignKey[T, P](this, pt)

}

trait MetaChildEntity[P <: Entity[P], T <: ChildEntity[P, T]] extends ChildEntity[P, T] with MetaEntity[T] {
  self: T =>
}


trait NamedEntity[T <: NamedEntity[T]] extends Entity[T] {
  self: T =>

  object name extends MappedText[T](this) {
    override def validations: List[String => List[FieldError]] =
      ((v: String) => v match {
        case "" => FieldError(this, Text("Name is required")) :: Nil
        case _ => Nil
      }) :: super.validations
  }

}

trait MetaNamedEntity[T <: NamedEntity[T]] extends NamedEntity[T] with MetaEntity[T] {
  self: T =>
}
