package com.github.david04.liftutils.entity

import com.github.david04.liftutils.elem.Editor
import com.github.david04.liftutils.datatables.Col

trait StdEntityBase {

  def id: Long

  def entityName: String

  def singular: String

  def plural: String

  def create: StdEntityBase
}

trait StdEntity[T <: StdEntity[T]] extends StdEntityBase {
  self: T =>

  def save(): T

  def delete(): Unit

  def elems(implicit editor: Editor): List[com.github.david04.liftutils.elem.EditableElem]

  def columns: List[Col[T]]

  def create: T
}