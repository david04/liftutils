package com.github.david04.liftutils.elem

import net.liftweb.common._
import net.liftweb.util.Helpers._
import net.liftweb.http._
import net.liftweb.http.js.JsCmd
import net.liftweb.http.js.JsCmds._
import scala.xml.NodeSeq
import net.liftweb.http.{SHtml}
import net.liftweb.util.{ClearNodes, PassThru}
import com.github.david04.liftutils.util.Util.__print


trait HTMLEditor extends ID {

  type E <: HTMLEditableElem

  protected var modified: Boolean = false

  private[elem] def framework: Framework

  protected def buildElems(): Seq[E]

  protected def editorAllTemplate = Templates("templates-hidden" :: "editor-all" :: Nil).get

  protected val elems = buildElems()

  def locPrefix: String

  protected def isValid: Boolean = fieldError().isEmpty

  protected def fieldError(): Option[NodeSeq] = elems.flatMap(_.error).headOption

  protected def onSubmit() =
    if (fieldError().isDefined) {
      elems.map(_.update()).reduceOption[JsCmd](_ & _).getOrElse(Noop)
    } else {
      elems.foreach(_.save())
      savedInternal()
    }

  def submitBtnTransforms: NodeSeq => NodeSeq = ".editor-btn-submit [onclick]" #> {submitForm()}

  lazy val submitBtnRenderer = SHtml2.idMemoize2(_ => submitBtnTransforms)

  def submitForm(): JsCmd =
    S.fmapFunc(() => onSubmit())(name => Run("liftAjax.lift_uriSuffix = '" + name + "=_'; $('#" + id('form) + "').submit();"))

  def renderEditor =
    ".editor-all" #> editorAllTemplate andThen
      ".editor-elems" #> elems.map(elem => <div class={s"editor-elem-${elem.elemName}"}></div>) andThen
      elems.map(elem => s".editor-elem-${elem.elemName}" #> elem.renderElemEditor).reduceOption(_ & _).getOrElse(PassThru) andThen
      ".editor-form [id]" #> id('form) andThen
      ".editor-btn-submit" #> submitBtnRenderer andThen
      SHtml.makeFormsAjax

  def renderedNoBtns = (".editor-btn-submit" #> ClearNodes).apply(renderEditor(<div class="editor-all"></div>))

  protected def savedInternal(): JsCmd = {
    modified = false
    saved()
  }

  protected def saved(): JsCmd = Noop

  private[elem] def elemChanged(elem: E): JsCmd = {
    modified = true
    (elems.map(_.update()) :+ Noop).reduce(_ & _)
  }

}

trait GlobalValidatableHTMLEditor extends HTMLEditor {

  protected def globalError(): Option[NodeSeq] = None

  override protected def isValid: Boolean = fieldError().isEmpty && globalError().isEmpty

  override protected def onSubmit() =
    if (fieldError().isDefined) {
      elems.map(_.update()).reduceOption[JsCmd](_ & _).getOrElse(Noop)
    } else globalError() match {
      case Some(error) =>
        SetHtml(id('globalVal), error) & JsShowId(id('globalVal))
      case None =>
        elems.foreach(_.save())
        savedInternal()
    }

  override def renderEditor = super.renderEditor andThen ".editor-global-validation [id]" #> id('globalVal)
}

trait SemanticSubmitButtonHTMLEditor extends HTMLEditor {

  override def submitBtnTransforms: NodeSeq => NodeSeq =
    super.submitBtnTransforms andThen
      ".editor-btn-submit [class+]" #> {
        if (!isValid) framework.btnDanger
        else if (modified) framework.btnSuccess
        else framework.btnMute
      }

  override protected def savedInternal(): JsCmd = super.savedInternal() & submitBtnRenderer.replaceHtml()

  override private[elem] def elemChanged(elem: E): JsCmd = super.elemChanged(elem) & submitBtnRenderer.replaceHtml()

}

trait DefaultHTMLEditor extends GlobalValidatableHTMLEditor with SemanticSubmitButtonHTMLEditor {
  type E = HTMLEditableElem
}

trait DefaultBS3HTMLEditor extends DefaultHTMLEditor with Bootstrap3 {
  def framework = new Bootstrap3 {}
  implicit def editor = this
}

trait EditableElem2DefaultEditorBridge extends HTMLEditableElem {

  protected def framework: Framework = editor.framework

  protected def editor: DefaultHTMLEditor

  protected def locPrefix = editor.locPrefix

  override protected def submit(): JsCmd = super.submit() & editor.submitForm()

  override protected def onChangeServerSide(): JsCmd = super.onChangeServerSide() & editor.elemChanged(this)

}
