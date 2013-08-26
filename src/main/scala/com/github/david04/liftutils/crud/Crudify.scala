//package com.github.david04.liftutils.crud
//
//import net.liftweb.json.JsonAST.JString
//import net.liftweb.http.{Templates, SessionVar, S}
//import net.liftweb.util._
//import Helpers._
//import net.liftweb.common.{Box, Full}
//import net.liftweb.http.js.JsCmds.{Run, RedirectTo}
//import net.liftweb.mapper._
//import net.liftweb.json.JsonAST.JString
//import net.liftweb.json.JsonAST.JString
//import com.github.david04.liftutils.forms.{FormField, Form}
//import com.github.david04.liftutils.datatables.{Table, Col}
//import com.github.david04.liftutils.entity.{ChildEntity, MetaEntity, Entity}
//import net.liftweb.http.js.JsCmd
//import com.github.david04.liftutils.datatables.Col
//import scala.Some
//import net.liftweb.json.JsonAST.JString
//import net.liftweb.http.js.JsCmds.Run
//import com.github.david04.liftutils.util.Util._
//import com.github.david04.liftutils.datatables.Col
//import scala.Some
//import net.liftweb.json.JsonAST.JString
//import net.liftweb.http.js.JsCmds.Run
//
//trait InContext[E <: Entity[E]] {
//  self: MetaEntity[E] =>
//
//  def onSetContext(nw: Option[E]) {}
//
//  object context extends SessionVar[Option[E]](None) {
//
//    override def __nameSalt = self.dbTableName
//
//    override def apply(what: Option[E]): Option[E] = {
//      onSetContext(what)
//      super.apply(what)
//    }
//  }
//
//}
//
//trait Editable[E <: Entity[E]] extends Entity[E] {
//  self: E =>
//
//  def fullName = getClass.getSimpleName.filter(_.isLetter)
//
//  def shortName = fullName
//
//  final def crudId: String = this.getClass.getSimpleName.filter(_.isLetter).toLowerCase
//
//  def fFields: List[FormField[E]] = Nil
//}
//
//trait MetaEditable[E <: Editable[E]] extends MetaEntity[E] with Editable[E] {
//  self: E =>
//
//  def newInstance(): E
//}
//
//trait Crudable[E <: Crudable[E]] extends Editable[E] {
//  self: E =>
//
//  def getSingleton: MetaCrudable[E]
//
//  def editParams: String
//
//  def listHeader: String
//
//  def listIcon: String
//
//  def listCreateButton: String
//
//  def createHeader: String
//
//  def editHeader: String
//
//  def formTemplate: List[String]
//}
//
//trait MetaCrudable[E <: Crudable[E]] extends Crudable[E] with MetaEditable[E] with InContext[E] {
//  self: E =>
//
//  def table(open: E => JsCmd): Table[E]
//
//  def createForm(): Form[E]
//
//  def editForm(param: String => Box[String]): Form[E]
//
//  def data(): Seq[E]
//}
//
//trait DefaultCrudable[E <: Crudable[E]] extends Crudable[E] {
//  self: E =>
//
//  import com.github.david04.liftutils.util.Util._
//
//  def cap = crudId.capitalize
//
//  // LIST:
//  def listHeader: String
//
//  def listCreateButton = s"New $cap"
//
//  def listEditButton = "Edit"
//
//  def listDeleteButton = "Delete"
//
//  // CREATE:
//  def createHeader = s"New $cap"
//
//  def createPrimaryBtn = "Create"
//
//  def createCancelBtn = "Cancel"
//
//  // CREATE:
//  def unsavedChildPrimaryBtn = "Done"
//
//  def unsavedChildCancelBtn = "Back"
//
//  // EDIT:
//  def editHeader = s"Edit $cap"
//
//  def editPrimaryBtn = "Save Changes"
//
//  def editCancelBtn = "Cancel"
//
//  def formTemplate = "templates-crud-hidden" :: "Form" :: Nil
//
//  def tableCols: List[Col[E]]
//
//  def editParams =
//    this match {
//      case child: ChildEntity[_, E] if (!saved_?) => s"idx=${child.all(child.parent.obj.open_!).indexWhere(_ eq this)}"
//      case _ => s"id=${primaryKeyField.get}"
//    }
//
//}
//
//trait MetaDefaultCrudable[E <: DefaultCrudable[E]] extends DefaultCrudable[E] with MetaCrudable[E] {
//  self: E =>
//
//  val formTemplateNodeSeq = Templates(formTemplate).openOrThrowException("")
//
//  private def transientParent_?(e: E) =
//    e match {case c: ChildEntity[_, E] => !c.parent.obj.open_!.saved_? case _ => false }
//
//  private def deleteE(e: E) {
//    e match {
//      case child: ChildEntity[_, E] if (!child.parent.obj.open_!.saved_?) => child.all(child.parent.obj.open_!) -= e
//      case _ => e.delete_!
//    }
//    context.set(None)
//  }
//
//  private def saveE(e: E): Unit = {
//    e match {
//      case child: ChildEntity[_, E] if !child.parent.obj.open_!.saved_? => {
//        val all = child.all(child.parent.obj.open_!)
//        if (!all.exists(_ eq e)) all += (e)
//        context.set(None)
//      }
//      case _ => e.save()
//    }
//  }
//
//  def table(open: E => JsCmd) = {
//    new Table[E](
//      data _
//    ) {
//
//      def deleteBtn(e: E) = JString(<a href="#" onclick={run {deleteE(e); reload()}}>
//        {listDeleteButton}
//      </a>.toString())
//
//      def editBtn(e: E) = JString(<a href="#" onclick={open(e).toJsCmd + "; return false;"}>
//        {listEditButton}
//      </a>.toString())
//
//      val columns =
//        tableCols :::
//          (Col[E]("Edit", c => editBtn(c), centerH = true, centerR = true) ::
//            Col[E]("Delete", c => deleteBtn(c), centerH = true, centerR = true) ::
//            Nil)
//    }
//  }
//
//  private def form(primaryBtn: String, cancelBtn: String, e: E) =
//    new Form[E](e) {
//
//      def template = formTemplateNodeSeq
//
//      val primaryBtnText = if (transientParent_?(e)) unsavedChildPrimaryBtn else primaryBtn
//      val cancelBtnText = if (transientParent_?(e)) unsavedChildCancelBtn else cancelBtn
//      val onCancel = Run("history.back();")
//      val onSuccess = Run("history.back();")
//      val fields = fFields
//
//      protected def save() { saveE(e) }
//
//    }
//
//  private def inContext(e: E) = context.set(Some(e)).get
//
//  def createForm = context.get match {
//    case Some(i) if !i.saved_? => form(createPrimaryBtn, createCancelBtn, i)
//    case _ => form(createPrimaryBtn, createCancelBtn, inContext(newInstance()))
//  }
//
//  def editForm(param: String => Box[String]) =
//    param("id").map(id => form(editPrimaryBtn, editCancelBtn, inContext(data.find(_.primaryKeyField.get.toString == id).get)))
//      .getOrElse(param("idx").map(idx => form(editPrimaryBtn, editCancelBtn, inContext(data()(idx.toInt)))).get)
//
//}
