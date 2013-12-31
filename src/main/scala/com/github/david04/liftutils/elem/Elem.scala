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
import com.github.david04.liftutils.Loc.Loc

trait ID {
  private val _id = S.formFuncName

  def id(part: Symbol) = _id + "-" + part.name

  def sel(part: Symbol, func: String = "") = "$('#" + id(part) + "')" + func
}

trait Elem extends ID {}

trait ValidatableElem extends Elem {
  def error(): Option[NodeSeq] = None
}

trait ViewableElem extends Elem {}

trait NodeSeqViewableElem extends ViewableElem {def renderNodeSeqView: NodeSeq}

trait NamedElem extends ViewableElem {def elemName: String}

trait LabeledElem extends NamedElem with Loc {
  def labelStr: String = loc(s"elem-lbl-$elemName")
  def labelStrOpt(suffix: String): Option[String] = locOpt(s"elem-lbl-$elemName-$suffix")
  def labelStr(suffix: String): String = loc(s"elem-lbl-$elemName-$suffix")
  def glabelStr(suffix: String): String = loc(s"elem-lbl-$suffix")
}

trait EditableElem extends ViewableElem with ValidatableElem with NamedElem {

  protected def framework: Framework

  private[elem] val enabled: () => Boolean

  private[elem] def save(): Unit
}

trait UpdatableElem extends ViewableElem {

  private[elem] def update(): JsCmd
}

trait HTMLViewableElem extends ViewableElem with NamedElem with UpdatableElem with Loc {

  protected def htmlElemTemplatePath: List[String] = "templates-hidden" :: "elem-view-dflt" :: Nil

  protected lazy val htmlElemTemplate = Templates(htmlElemTemplatePath).get

  protected lazy val htmlElemRenderer = SHtml.idMemoize(_ => htmlElemRendererTransforms)

  protected def htmlElemRendererTransforms: NodeSeq => NodeSeq = PassThru

  protected def rerenderHtmlElem(): JsCmd = htmlElemRenderer.setHtml()

  private[elem] def renderElem: NodeSeq = htmlElemRenderer.apply(htmlElemTemplate)

  protected def wrapName(name: String) = name + ": "

  private[elem] def update(): JsCmd = Noop
}

trait HTMLEditableElem extends HTMLViewableElem with EditableElem {

  override protected def htmlElemTemplatePath: List[String] = "templates-hidden" :: "elem-edit-dflt" :: Nil

  protected def onChangeServerSide(): JsCmd = Noop

  protected def submit(): JsCmd = Noop

  override private[elem] def update() =
    super.update() &
      (if (enabled())
        Run(sel('wrapper) + ".fadeIn(300);") &
          (error.map(error => Run(
            sel('error) + ".html(" + error.toString.encJs + "); " +
              sel('wrapper) + ".addClass(" + framework.errorClass.encJs + "); "))
            .getOrElse(Run(
            sel('error) + ".html(''); " +
              sel('wrapper) + ".removeClass(" + framework.errorClass.encJs + "); ")))
      else
        Run(sel('wrapper) + ".fadeOut();"))
}
