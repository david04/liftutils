package com.github.david04.liftutils.crud

import net.liftweb.json.JsonAST.JString
import net.liftweb.http.S
import net.liftweb.util._
import Helpers._
import net.liftweb.common.Full
import net.liftweb.http.js.JsCmds.RedirectTo
import net.liftweb.mapper.{LongKeyedMetaMapper, MetaMapper, KeyedMapper, Mapper}
import net.liftweb.common.Full
import net.liftweb.json.JsonAST.JString
import net.liftweb.common.Full
import net.liftweb.json.JsonAST.JString
import com.github.david04.liftutils.forms.{FormField, Form, MappedTextFormField}
import com.github.david04.liftutils.datatables.{Table, Col}

trait Crud {
  def name: String

  def listHeader: String
  def listIcon: String
  def listCreateButton: String
  def createHeader: String
  def editHeader: String

  def table: Table[_]

  def create: Form[_]

  def edit(id: String): Form[_]
}

abstract class DefaultCrud[T <: KeyedMapper[Long, T]] extends Crud {

  import com.github.david04.liftutils.util.Util._

  def cap = name.capitalize
  // LIST:
  def listHeader: String
  def listCreateButton = s"New $cap"
  def listEditButton = "Edit"
  def listDeleteButton = "Delete"
  // CREATE:
  def createHeader = s"New $cap"
  def createPrimaryBtn = "Create"
  // EDIT:
  def editHeader = s"Edit $cap"
  def editPrimaryBtn = "Save Changes"

  def newInstance(): T

  def data(): Seq[T]

  def tableCols: List[Col[T]]

  def formFields: List[FormField[_, T]]

  // ============ IMPLEMENTATION: ============

  def table = {
    new Table[T](
      data _
    ) {

      def deleteBtn(e: T) = JString(<a href="#" onclick={run {e.delete_!; reload()}}>
        {listDeleteButton}
      </a>.toString())

      def editBtn(e: T) = JString(<a href={s"/app/crud/edit?crud=$name&id=${e.primaryKeyField.get}"}>
        {listEditButton}
      </a>.toString())

      val columns =
        tableCols :::
          (Col[T]("Edit", c => editBtn(c), centerH = true, centerR = true) ::
            Col[T]("Delete", c => deleteBtn(c), centerH = true, centerR = true) ::
            Nil)
    }
  }

  private def form(primaryBtn: String, e: T) =
    new Form[T](e) {

      val primaryBtnText = primaryBtn
      val back = s"/app/crud/list?crud=$name"
      val fields = formFields
    }

  def create = form(createPrimaryBtn, newInstance())

  def edit(id: String) = form(editPrimaryBtn, data.find(_.primaryKeyField.get.toString == id).get)

}

