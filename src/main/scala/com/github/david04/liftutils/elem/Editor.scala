package com.github.david04.liftutils.elem

import net.liftweb.util.Helpers._
import net.liftweb.http.js.JsCmd
import net.liftweb.http.js.JsCmds._
import scala.xml.NodeSeq
import net.liftweb.http.{AjaxHelpers, SHtml}


trait Editor extends ID {

  def buildElems(): Seq[HTMLEditableElem]

  val elems = buildElems()

  def renderElems =
    ".editor .editor-elems" #> elems.map(elem => elem.edit).reduceOption(_ ++ _).getOrElse(NodeSeq.Empty) &
      ".editor [id]" #> id('editor) &
      ".editor-form [id]" #> id('form) andThen
      SHtml.makeFormsAjax

  protected def saved(): JsCmd

  private[elem] def elemChanged(elem: HTMLEditableElem): JsCmd = (elems.map(_.update()) :+ Noop).reduce(_ & _)

  def renderSubmitBtn =
    AjaxHelpers.ajaxOnSubmitTo(id('form))(() => {
      if (elems.exists(_.error.isDefined)) {
        elems.map(_.update()).reduceOption[JsCmd](_ & _).getOrElse(Noop)
      } else {
        elems.foreach(_.save())
        saved()
      }
    })
}

trait EditableElem2EditorBridge extends HTMLEditableElem {

  protected def editor: Editor

  protected def onChangeServerSide(): JsCmd = editor.elemChanged(this)

}
