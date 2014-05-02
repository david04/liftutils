package com.github.david04.liftutils.modal

import net.liftweb.util._
import net.liftweb.util.Helpers._
import net.liftweb.http._
import scala.xml.NodeSeq
import net.liftweb.http.js.JsCmds.Run
import net.liftweb.http.js.JsCmd

trait NiftyModal {
  protected lazy val id = S.formFuncName
  protected lazy val templateLoc = "templates-hidden" :: "nifty-modal" :: "modal-dflt" :: Nil
  protected lazy val template = modalTransforms.apply(Templates(templateLoc).get)

  protected def title: String
  protected def content: NodeSeq
  protected def cancel: Option[(String, JsCmd)]
  protected def action: Option[(String, JsCmd)]

  def modalActionBtnTransforms: NodeSeq => NodeSeq =
    action.map({
      case (lbl, jsCmd) =>
        ".mdl-action *" #> lbl &
          ".mdl-action [onclick]" #> jsCmd
    }).getOrElse(".mdl-action" #> ClearNodes)

  def modalCancelBtnTransforms: NodeSeq => NodeSeq =
    cancel.map({
      case (lbl, jsCmd) =>
        ".mdl-cancel *" #> lbl &
          ".mdl-cancel [onclick]" #> jsCmd
    }).getOrElse(".mdl-cancel" #> ClearNodes)

  def modalTransforms: NodeSeq => NodeSeq =
    ".mdl [id]" #> id &
      ".mdl-title *" #> title &
      ".mdl-contents" #> content &
      ".mdl-cancel" #> modalCancelBtnTransforms andThen
      ".mdl-action" #> modalActionBtnTransforms

  def show() =
    Run(s"if(document.getElementById('$id') == null) " +
      "$(" + template.toString.encJs + ").appendTo('body');") &
      Run("$('#" + id + "').niftyModal('show');")

  def hide() = Run("$('#" + id + "').niftyModal('hide');")
}

case class DefaultNiftyModal(
                         protected val title: String,
                         protected val content: NodeSeq,
                         protected val cancelLbl: String,
                         protected val action: Option[(String, JsCmd)],
                         protected val height: Option[Int] = None
                         ) extends Modal {

  def cancel = Some(cancelLbl, hide())
}
