package com.github.david04.liftutils.elem

import net.liftweb.util.Helpers._
import net.liftweb.http._
import net.liftweb.http.js.JsCmd
import net.liftweb.http.js.JsCmds._
import scala.xml.NodeSeq
import net.liftweb.http.{AjaxHelpers, SHtml}
import net.liftweb.util.PassThru


trait HTMLEditor extends ID {

  type E <: HTMLEditableElem

  protected def buildElems(): Seq[E]

  protected def editorAllTemplate = Templates("templates-hidden" :: "editor-all" :: Nil).get

  protected val elems = buildElems()

  def locPrefix: String

  protected def onSubmit() =
    if (elems.exists(_.error.isDefined)) {
      elems.map(_.update()).reduceOption[JsCmd](_ & _).getOrElse(Noop)
    } else {
      elems.foreach(_.save())
      saved()
    }

  def renderEditor =
    ".editor-all" #> editorAllTemplate andThen
      ".editor-elems" #> elems.map(elem => <div class={s"editor-elem-${elem.elemName}"}></div>) andThen
      elems.map(elem => s".editor-elem-${elem.elemName}" #> elem.renderElemEditor).reduceOption(_ & _).getOrElse(PassThru) andThen
      ".editor-form [id]" #> id('form) andThen
      ".editor-btn-submit" #> AjaxHelpers.ajaxOnSubmitTo(id('form))(() => onSubmit()) andThen
      SHtml.makeFormsAjax

  protected def saved(): JsCmd

  private[elem] def elemChanged(elem: E): JsCmd = (elems.map(_.update()) :+ Noop).reduce(_ & _)

}

trait GlobalValidatableHTMLEditor extends HTMLEditor {

  protected def globalError(): Option[NodeSeq] = None

  override protected def onSubmit() =
    if (elems.exists(_.error.isDefined)) {
      elems.map(_.update()).reduceOption[JsCmd](_ & _).getOrElse(Noop)
    } else globalError() match {
      case Some(error) =>
        SetHtml(id('globalVal), error) & JsShowId(id('globalVal))
      case None =>
        elems.foreach(_.save())
        saved()
    }

  override def renderEditor = super.renderEditor andThen ".editor-global-validation [id]" #> id('globalVal)
}

trait DefaultHTMLEditor extends GlobalValidatableHTMLEditor {
  type E = HTMLEditableElem
}

trait EditableElem2DefaultEditorBridge extends HTMLEditableElem {

  protected def editor: DefaultHTMLEditor

  protected def locPrefix = editor.locPrefix

  override protected def onChangeServerSide(): JsCmd = super.onChangeServerSide() & editor.elemChanged(this)

}
