package com.github.david04.liftutils.entity

import com.github.david04.liftutils.elem.{DefaultHTMLEditor, HTMLEditor}
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

  def elems(implicit editor: DefaultHTMLEditor): List[com.github.david04.liftutils.elem.HTMLEditableElem]

  def columns: List[Col[T]]

  def create: T
}