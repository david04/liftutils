package com.github.david04.liftutils.elem

import com.github.david04.liftutils.modal.Modal
import net.liftweb.util.Helpers._
import net.liftweb.http.{SHtml, Templates}
import scala.xml.NodeSeq
import net.liftweb.util.PassThru
import net.liftweb.http.js.JsCmd
import com.github.david04.liftutils.util.Util.__print
import net.liftweb.http.js.JsCmds.{SetValById, Run}


trait ModalEditElem extends HTMLEditableElem with Modal with LabeledElem {

  protected def htmlModalEditableElemViewTemplatePath: List[String] = "templates-hidden" :: "elem-modaledit-dflt-view" :: Nil

  protected lazy val htmlModalEditableElemViewTemplate = Templates(htmlModalEditableElemViewTemplatePath).get

  protected lazy val htmlModalEditableElemViewRenderer = SHtml.idMemoize(_ => htmlModalEditableElemViewRendererTransforms)

  protected def getCurrentViewString(): String
  protected def setCurrentViewString(s: String): JsCmd = Run("$('#" + id('vinput) + "').attr('value', " + s.encJs + ");")

  protected def htmlModalEditableElemViewRendererTransforms: NodeSeq => NodeSeq =
    ".elem-wrap [style+]" #> (if (!enabled()) "display:none;" else "") &
      ".elem-wrap [id]" #> id('vwrapper) &
      ".elem-lbl *" #> wrapName(labelStr) &
      ".elem-error [id]" #> id('verror) &
      ".elem-lbl *" #> wrapName(labelStr) &
      "input [value]" #> getCurrentViewString() &
      "input [id]" #> id('vinput) &
      ".edit-btn [onclick]" #> SHtml.onEvent(_ => {
        show()
      })

  protected def rerenderHtmlModalEditableElemView(): JsCmd = htmlModalEditableElemViewRenderer.setHtml()

  private[elem] def renderModalEditableElemView: NodeSeq = htmlModalEditableElemViewRenderer.apply(htmlModalEditableElemViewTemplate)

  override private[elem] def renderElemEditor: NodeSeq = renderModalEditableElemView

  protected def action: Option[(String, net.liftweb.http.js.JsCmd)] = Some((glabelStr("done"), hide()))
  protected def cancel: Option[(String, net.liftweb.http.js.JsCmd)] = None
  protected def content: scala.xml.NodeSeq = super.renderElemEditor
  protected def height: Option[Int] = None
  protected def title: String = labelStr("modalTitle")

  override private[elem] def update() =
    super.update() & {
      if (enabled())
        Run(sel('vwrapper) + ".fadeIn(300);") &
          (error.map(error => Run(
            sel('verror) + ".html(" + error.toString.encJs + "); " +
              sel('vwrapper) + ".addClass(" + framework.errorClass.encJs + "); "))
            .getOrElse(Run(
            sel('verror) + ".html(''); " +
              sel('vwrapper) + ".removeClass(" + framework.errorClass.encJs + "); ")))
      else
        Run(sel('vwrapper) + ".fadeOut();")
    }
}
