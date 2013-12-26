package com.github.david04.liftutils.modal

import net.liftweb.util._
import net.liftweb.util.Helpers._
import net.liftweb.http._
import net.liftweb.http.js.jquery.JQuery14Artifacts
import scala.xml.NodeSeq
import net.liftweb.http.js.JsCmds.Run
import net.liftweb.http.js.JsCmd

trait Modal {
  protected lazy val id = S.formFuncName
  protected lazy val templateLoc = "templates-hidden" :: "modal-dflt" :: Nil
  protected lazy val template = modalTransforms.apply(Templates(templateLoc).get)

  protected def title: String
  protected def content: NodeSeq
  protected def cancel: Option[(String, JsCmd)]
  protected def action: Option[(String, JsCmd)]
  protected def height: Option[Int]

  def modalActionBtnTransforms: NodeSeq => NodeSeq =
    action.map({
      case (lbl, jsCmd) =>
        ".modal-action *" #> lbl &
          ".modal-action [onclick]" #> jsCmd
    }).getOrElse(".modal-action" #> ClearNodes)

  def modalCancelBtnTransforms: NodeSeq => NodeSeq =
    cancel.map({
      case (lbl, jsCmd) =>
        ".modal-cancel *" #> lbl &
          ".modal-cancel [onclick]" #> jsCmd
    }).getOrElse(".modal-cancel" #> ClearNodes)

  def modalTransforms: NodeSeq => NodeSeq =
    ".modal [id]" #> id &
      ".modal-title *" #> title &
      ".modal-contents" #> content &
      ".modal-cancel" #> modalCancelBtnTransforms andThen
      ".modal-action" #> modalActionBtnTransforms andThen
      ".modal-body .scroller [style]" #> height.map(h => s"height:${h}px").getOrElse("")

  def show() =
    Run(s"if(document.getElementById('$id') == null) " +
      "$(" + template.toString.encJs + ").appendTo('body');") &
      Run("$('#" + id + "').modal('show');")

  def hide() = Run("$('#" + id + "').modal('hide');")
}

case class DefaultModal(
                         protected val title: String,
                         protected val content: NodeSeq,
                         protected val cancelLbl: String,
                         protected val action: Option[(String, JsCmd)],
                         protected val height: Option[Int] = None
                         ) extends Modal {

  def cancel = Some(cancelLbl, hide())
}
