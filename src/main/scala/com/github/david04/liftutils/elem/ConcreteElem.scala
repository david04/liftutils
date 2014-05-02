package com.github.david04.liftutils.elem


import scala.xml.{UnprefixedAttribute, NodeSeq}
import net.liftweb.common._
import net.liftweb.http.{FileParamHolder, S, SHtml}
import net.liftweb.util.Helpers._
import net.liftweb.http.SHtml.ElemAttr
import net.liftweb.http.js.JE.{ValById, JsRaw}
import net.liftweb.http.S.{SFuncHolder, LFuncHolder}
import org.apache.commons.lang.StringEscapeUtils
import net.liftweb.json.JsonAST.{JNull, JString, JArray, JValue}

trait PasswordInputElem extends TextInputElem {

  override protected def inputElem: NodeSeq = ("input [type]" #> "password").apply(super.inputElem)
}

trait TextInputElem extends GenEditableStringValueElem with HTMLEditableElem with LabeledElem {

  protected def placeholder: Option[String]

  protected var value: String = getStringValue()

  def getCurrentStringValue(): String = value

  protected def textInputAttrs: Seq[ElemAttr]

  protected def classes: List[String] = Nil

  import ElemAttr._

  protected def inputElemDefaultAttrs: Seq[ElemAttr] = Seq[ElemAttr](
    ("id" -> id('input)),
    ("placeholder" -> placeholder.getOrElse("")),
    ("onchange" -> ("{" + SHtml.onEvent(v => {value = v; onChangeClientSide()}).toJsCmd + "; return true; }")),
    ("onkeyup" -> ("{if (window.event.keyCode == 13) {" + SHtml.ajaxCall(ValById(id('input)), v => {value = v; onChangeClientSide() & submit()}).toJsCmd + "; }}"))
  )

  protected def inputElem: NodeSeq = SHtml.text(value, value = _, textInputAttrs ++ inputElemDefaultAttrs: _*)

  override protected def htmlElemRendererTransforms =
    super.htmlElemRendererTransforms andThen (
      ".elem-wrap [style+]" #> (if (!enabled()) "display:none;" else "") &
        ".elem-wrap [id]" #> id('wrapper) &
        ".elem-lbl *" #> wrapName(labelStr) &
        ".elem-error [id]" #> id('error)
      ) andThen
      ((ns: NodeSeq) => bind("elem", ns, "input" -%> inputElem))
}

trait TextAreaInputElem extends TextInputElem {
  override protected def inputElem: NodeSeq = SHtml.textarea(value, value = _, textInputAttrs ++ inputElemDefaultAttrs: _*)
}

trait TextViewerElem extends GenStringValueElem with HTMLViewableElem with LabeledElem {

  protected def classes: List[String] = Nil


  override protected def htmlElemRendererTransforms =
    super.htmlElemRendererTransforms andThen
      ".elem-wrap [id]" #> id('wrapper) &
        ".elem-lbl *" #> wrapName(labelStr) &
        ".elem-error [id]" #> id('error) &
        ".elem-value *" #> getStringValue()
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
          {StringEscapeUtils.unescapeHtml(text.text)}
        </option>) % selected(deflt.exists(_ == value))
      }}
    </select>))(_ % _)
  }

  private var value = getOneOfManyValue()

  def getCurrentOneOfManyValue() = value

  protected def selectInputAttrs: Seq[ElemAttr]

  override protected def htmlElemRendererTransforms =
    super.htmlElemRendererTransforms andThen (
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
            ("onchange" -> ("{" + SHtml.onEvent(v => {getAllOneOfManyValues().find(_.id == v).foreach(value = _); onChangeClientSide()}).toJsCmd + "; return true; }"))
          )): _*)
      ))
}

trait MultiSelectInputElem extends GenManyOfManyValueElem with HTMLEditableElem with LabeledElem {

  private var value = getManyOfManyValue()

  def getCurrentManyOfManyValue() = value

  protected def selectInputAttrs: Seq[ElemAttr]

  override def requiresIFrameSubmit(): Boolean = true

  override protected def htmlElemRendererTransforms =
    super.htmlElemRendererTransforms andThen (
      ".elem-wrap [style+]" #> (if (!enabled()) "display:none;" else "") &
        ".elem-wrap [id]" #> id('wrapper) &
        ".elem-lbl *" #> wrapName(labelStr) &
        ".elem-error [id]" #> id('error)
      ) andThen
      ((ns: NodeSeq) => bind("elem", ns, "input" -%>
        SHtml.multiSelectObj[String](
          getAllManyOfManyValues().map(v => (v.id, v.name.toString())),
          value.map(_.id),
          (v: List[String]) => {
            val map = getAllManyOfManyValues().map(v => (v.id, v)).toMap
            value = v.flatMap(map.get(_))
          },
          (selectInputAttrs ++ Seq[ElemAttr](
            ("id" -> id('input)),
            ("onchange" -> ("{" + SHtml.jsonCall(JsRaw(sel('input, ".val()")),
              (v: JValue) => v match {
                case JArray(lst) =>
                  val map = getAllManyOfManyValues().map(v => (v.id, v)).toMap
                  value = lst.collect({case JString(v) => v}).flatMap(map.get(_))
                  onChangeClientSide()
                case JNull =>
                  value = Seq()
                  onChangeClientSide()
                case other =>
                  println(other)
                  ???
              }).toJsCmd + "; return true; }"))
          )): _*)
      ))
}

trait FileUploadInputElem extends GenFileOptValueElem with HTMLEditableElem with LabeledElem {

  private var value: Option[(Array[Byte], String)] = None

  def getFile() = value

  protected def fileInputAttrs: Seq[ElemAttr]

  override def requiresIFrameSubmit = true

  override protected def htmlElemRendererTransforms =
    super.htmlElemRendererTransforms andThen (
      ".elem-wrap [style+]" #> (if (!enabled()) "display:none;" else "") &
        ".elem-wrap [id]" #> id('wrapper) &
        ".elem-lbl *" #> wrapName(labelStr) &
        ".elem-error [id]" #> id('error)
      ) andThen
      ((ns: NodeSeq) => bind("elem", ns, "input" -%>
        SHtml.fileUpload(
          (file: FileParamHolder) => {
            value = Some((file.file, file.fileName))
          },
          (fileInputAttrs ++ Seq[ElemAttr](
            ("id" -> id('input))
          )): _*)
      ))
}

trait CheckboxInputElem extends GenEditableBooleanValueElem with HTMLEditableElem with LabeledElem {

  override protected def wrapName(name: String) = name

  private var value = getBooleanValue()

  def getCurrentBooleanValue() = value

  protected def checkboxInputAttrs: Seq[ElemAttr]

  override protected def htmlElemRendererTransforms =
    super.htmlElemRendererTransforms andThen (
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
              v => {value = v.toBoolean; onChangeClientSide()}).toJsCmd + "; return true; }"))
          )): _*)
      ))
}

trait IconElem extends HTMLEditableElem {def icon: String}

trait HTMLIIconElem extends IconElem {
  override def htmlElemRendererTransforms =
    super.htmlElemRendererTransforms andThen
      "i [class]" #> s"$icon"
}

