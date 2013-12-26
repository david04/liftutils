package com.github.david04.liftutils.elem

import scala.xml.NodeSeq
import scala.xml.Text
import net.liftweb.common._
import net.liftweb.http.S
import net.liftweb.http.js.JsCmd
import net.liftweb.http.js.JsCmds._
import net.liftweb.util.Helpers._
import net.liftweb.http.{Templates, SHtml}
import java.util.UUID
import java.net.InetAddress
import scala.concurrent.duration.Duration
import java.util.regex.Pattern
import net.liftweb.http.SHtml.ElemAttr
import scala.util.Try
import net.liftweb.http.js.JE.JsRaw
import com.github.david04.liftutils.util.Util.__print
import net.liftweb.util.PassThru

trait ID {
  private val _id = S.formFuncName

  def id(part: Symbol) = _id + "-" + part.name

  def sel(part: Symbol) = "$('#" + id(part) + "')"
}

trait Elem extends ID {}

trait Framework {

  def errorClass: String
  def warningClass: String
  def successClass: String

  def btnDefault: String
  def btnMute: String
  def btnPrimary: String
  def btnSuccess: String
  def btnInfo: String
  def btnWarning: String
  def btnDanger: String
}

trait ValidatableElem extends Elem {
  def error(): Option[NodeSeq] = None
}

trait ViewableElem extends Elem {}

trait NodeSeqViewableElem extends ViewableElem {def renderNodeSeqView: NodeSeq}

trait NamedElem extends ViewableElem {def elemName: String}

trait LocPrefixedElem extends Elem {protected def locPrefix: String}

trait LabeledElem extends NamedElem with LocPrefixedElem {
  def labelStr: String = S.?(s"$locPrefix-elem-lbl-$elemName")
  def labelStr(suffix: String): String = S.?(s"$locPrefix-elem-lbl-$elemName-$suffix")
  def glabelStr(suffix: String): String = S.?(s"$locPrefix-elem-lbl-$suffix")
}

trait EditableElem extends ValidatableElem with NamedElem {

  protected def framework: Framework

  private[elem] val enabled: () => Boolean

  private[elem] def save(): Unit
}

trait HTMLEditableElem extends EditableElem with LocPrefixedElem {

  protected def htmlEditableElemTemplatePath: List[String] = "templates-hidden" :: "elem-edit-dflt" :: Nil

  protected lazy val htmlEditableElemTemplate = Templates(htmlEditableElemTemplatePath).get

  protected lazy val htmlEditableElemRenderer = SHtml.idMemoize(_ => htmlEditableElemRendererTransforms)

  protected def htmlEditableElemRendererTransforms: NodeSeq => NodeSeq = PassThru

  protected def rerenderHtmlEditableElem(): JsCmd = htmlEditableElemRenderer.setHtml()

  private[elem] def renderElemEditor: NodeSeq = htmlEditableElemRenderer.apply(htmlEditableElemTemplate)

  protected def onChangeServerSide(): JsCmd = Noop

  protected def submit(): JsCmd = Noop

  protected def wrapName(name: String) = name + ": "

  private[elem] def update() =
    if (enabled())
      Run(sel('wrapper) + ".fadeIn(300);") &
        (error.map(error => Run(
          sel('error) + ".html(" + error.toString.encJs + "); " +
            sel('wrapper) + ".addClass(" + framework.errorClass.encJs + "); "))
          .getOrElse(Run(
          sel('error) + ".html(''); " +
            sel('wrapper) + ".removeClass(" + framework.errorClass.encJs + "); ")))
    else
      Run(sel('wrapper) + ".fadeOut();")
}
