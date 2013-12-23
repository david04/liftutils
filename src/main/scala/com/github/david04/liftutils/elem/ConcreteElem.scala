package com.github.david04.liftutils.elem


import scala.xml.{UnprefixedAttribute, NodeSeq, Text}
import net.liftweb.common._
import net.liftweb.http.S
import net.liftweb.util.Helpers._
import net.liftweb.http.SHtml
import java.net.InetAddress
import scala.concurrent.duration.Duration
import java.util.regex.Pattern
import net.liftweb.http.SHtml.ElemAttr
import scala.util.Try
import net.liftweb.http.js.JE.{ValById, JsVal, JsRaw}
import net.liftweb.http.S.{SFuncHolder, LFuncHolder, AFuncHolder}
import net.liftweb.http.js.JsCmds.{Run, OnLoad, Script}

trait PasswordInputElem extends TextInputElem {

  override protected def inputElem: NodeSeq = ("input [type]" #> "password").apply(super.inputElem)
}

trait TextInputElem extends GenEditableStringValueElem with HTMLEditableElem with LabeledElem {

  protected def placeholder: Option[String]

  private var value: String = getStringValue()

  def getCurrentStringValue(): String = value

  protected def textInputAttrs: Seq[ElemAttr]

  protected def classes: List[String] = Nil

  import ElemAttr._

  protected def inputElem: NodeSeq = SHtml.text(value, value = _,
    textInputAttrs ++ Seq[ElemAttr](
      ("id" -> id('input)),
      ("placeholder" -> placeholder.getOrElse("")),
      ("onchange" -> ("{" + SHtml.onEvent(v => {value = v; onChangeServerSide()}).toJsCmd + "; return true; }")),
      ("onkeyup" -> ("{if (window.event.keyCode == 13) {" + SHtml.ajaxCall(ValById(id('input)), v => {value = v; onChangeServerSide() & submit()}).toJsCmd + "; }}"))
    ): _*)

  override protected def htmlEditableElemRendererTransforms =
    super.htmlEditableElemRendererTransforms andThen (
      ".elem-wrap [style+]" #> (if (!enabled()) "display:none;" else "") &
        ".elem-wrap [id]" #> id('wrapper) &
        ".elem-lbl *" #> wrapName(labelStr) &
        ".elem-error [id]" #> id('error)
      ) andThen
      ((ns: NodeSeq) => bind("elem", ns, "input" -%> inputElem))
}

trait SelectInputElem extends GenOneOfManyValueElem with HTMLEditableElem with LabeledElem {

  def select(opts: Seq[(String, NodeSeq)], deflt: Box[String],
             _func: String => Any, attrs: ElemAttr*): scala.xml.Elem = {
    def selected(in: Boolean) =
      if (in) new UnprefixedAttribute("selected", "selected", scala.xml.Null) else scala.xml.Null

    val func = SFuncHolder(_func)
    val vals = opts.map(_._1)
    val testFunc = LFuncHolder(in => in.filter(v => vals.contains(v)) match {case Nil => false case xs => func(xs)}, func.owner)

    attrs.foldLeft(S.fmapFunc(testFunc)(fn => <select name={fn}>
      {opts.flatMap {
        case (value, text) => (<option value={value}>
          {text}
        </option>) % selected(deflt.exists(_ == value))
      }}
    </select>))(_ % _)
  }

  private var value = getOneOfManyValue()

  def getCurrentOneOfManyValue() = value

  protected def selectInputAttrs: Seq[ElemAttr]

  override protected def htmlEditableElemRendererTransforms =
    super.htmlEditableElemRendererTransforms andThen (
      ".elem-wrap [style+]" #> (if (!enabled()) "display:none;" else "") &
        ".elem-wrap [id]" #> id('wrapper) &
        ".elem-lbl *" #> wrapName(labelStr) &
        ".elem-error [id]" #> id('error)
      ) andThen
      ((ns: NodeSeq) => bind("elem", ns, "input" -%>
        select(
          getAllOneOfManyValues().map(v => (v.id, v.name)), Full(value.id),
          v => getAllOneOfManyValues().find(_.id == v).foreach(value = _),
          (selectInputAttrs ++ Seq[ElemAttr](
            ("id" -> id('input)),
            ("onchange" -> ("{" + SHtml.onEvent(v => {getAllOneOfManyValues().find(_.id == v).foreach(value = _); onChangeServerSide()}).toJsCmd + "; return true; }"))
          )): _*)
      ))
}

trait CheckboxInputElem extends GenEditableBooleanValueElem with HTMLEditableElem with LabeledElem {

  override protected def wrapName(name: String) = name

  private var value = getBooleanValue()

  def getCurrentBooleanValue() = value

  protected def checkboxInputAttrs: Seq[ElemAttr]

  override protected def htmlEditableElemRendererTransforms =
    super.htmlEditableElemRendererTransforms andThen (
      ".elem-wrap [style+]" #> (if (!enabled()) "display:none;" else "") &
        ".elem-wrap [id]" #> id('wrapper) &
        ".elem-lbl *" #> wrapName(labelStr) &
        ".elem-error [id]" #> id('error)
      ) andThen
      ((ns: NodeSeq) => bind("elem", ns, "input" -%>
        SHtml.checkbox(value, value = _,
          (checkboxInputAttrs ++ Seq[ElemAttr](
            ("id" -> id('input)),
            ("onchange" -> ("{" + SHtml.ajaxCall(JsRaw(sel('input) + ".is(':checked')"),
              v => {value = v.toBoolean; onChangeServerSide()}).toJsCmd + "; return true; }"))
          )): _*)
      ))
}

trait IconElem extends HTMLEditableElem {def icon: String}

trait HTMLIIconElem extends IconElem {
  override def htmlEditableElemRendererTransforms =
    super.htmlEditableElemRendererTransforms andThen
      "i [class]" #> s"icon-$icon"
}

