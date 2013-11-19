package com.github.david04.liftutils.elem

import net.liftweb.util.Helpers._
import net.liftweb.http._
import net.liftweb.http.js.JsCmd
import net.liftweb.http.js.JsCmds._
import scala.xml.NodeSeq
import net.liftweb.http.{AjaxHelpers, SHtml}
import net.liftweb.util.PassThru


trait HTMLEditor extends ID {

  def buildElems(): Seq[HTMLEditableElem]

  def editorAllTemplate = Templates("templates-hidden" :: "editor-all" :: Nil).get

  val elems = buildElems()

  def renderEditor =
    ".editor-all" #> editorAllTemplate andThen
      ".editor-elems" #> elems.map(elem => <div class={s"editor-elem-${elem.elemName}"}></div>) andThen
      elems.map(elem => s".editor-elem-${elem.elemName}" #> elem.edit).reduceOption(_ & _).getOrElse(PassThru) andThen
      ".editor-form [id]" #> id('form) andThen
      ".editor-btn-submit" #> AjaxHelpers.ajaxOnSubmitTo(id('form))(() => {
        if (elems.exists(_.error.isDefined)) {
          elems.map(_.update()).reduceOption[JsCmd](_ & _).getOrElse(Noop)
        } else {
          elems.foreach(_.save())
          saved()
        }
      }) andThen
      SHtml.makeFormsAjax

  protected def saved(): JsCmd

  private[elem] def elemChanged(elem: HTMLEditableElem): JsCmd = (elems.map(_.update()) :+ Noop).reduce(_ & _)

}

trait EditableElem2EditorBridge extends HTMLEditableElem {

  protected def editor: HTMLEditor

  protected def onChangeServerSide(): JsCmd = editor.elemChanged(this)

}
