package com.github.david04.liftutils.elem

import scala.xml.NodeSeq
import scala.xml.Text
import net.liftweb.common._
import net.liftweb.http.S
import net.liftweb.util.Helpers._
import net.liftweb.http.SHtml
import java.net.InetAddress
import scala.concurrent.duration.Duration
import java.util.regex.Pattern
import net.liftweb.http.SHtml.ElemAttr
import scala.util.Try
import net.liftweb.http.js.JE.JsRaw

// get***Value: in the server
// getCurrent***Value: in the client

trait GenDoubleValueElem extends Elem {def getDoubleValue(): Double}

trait GenEditableDoubleValueElem extends GenDoubleValueElem with ValidatableElem {def getCurrentDoubleValue(): Double}

trait GenBooleanValueElem extends Elem {def getBooleanValue(): Boolean}

trait GenEditableBooleanValueElem extends GenBooleanValueElem with ValidatableElem {def getCurrentBooleanValue(): Boolean}

trait GenStringValueElem extends Elem {def getStringValue(): String}

trait GenEditableStringValueElem extends GenStringValueElem with ValidatableElem {def getCurrentStringValue(): String}

trait GenEnumValueElem extends Elem {
  protected type EnumType <: Enumeration
  protected type EnumValueType = EnumType#Value

  protected def enum: EnumType

  def getEnumValue(): EnumValueType
}

trait GenSeqValueElem extends Elem {
  protected type SeqValueType

  protected def seq: Seq[SeqValueType]

  def getSeqValue(): SeqValueType
}

trait GenEditableEnumValueElem extends GenEnumValueElem with ValidatableElem {def getCurrentEnumValue(): EnumValueType}

trait GenEditableSeqValueElem extends GenSeqValueElem with ValidatableElem {def getCurrentSeqValue(): SeqValueType}

trait GenOneOfManyValueElem extends Elem {
  protected type OneOfManyValue <: Object {def name: String; def id: String}

  def getOneOfManyValue(): OneOfManyValue

  def getAllOneOfManyValues(): Seq[OneOfManyValue]
}

trait GenEditableOneOfManyValueElem extends GenOneOfManyValueElem with ValidatableElem {def getCurrentOneOfManyValue(): OneOfManyValue}

abstract class GenDouble2GenString extends GenEditableDoubleValueElem with GenEditableStringValueElem {

  protected def double2StringFormat = "%.2f"

  override private[elem] def error: Option[NodeSeq] =
    Try(getCurrentStringValue().toDouble).map(_ => super.error).getOrElse(Some(Text("Invalid value.")))

  def getCurrentDoubleValue() = getCurrentStringValue().toDouble

  def getStringValue() = double2StringFormat.format(getDoubleValue())
}

abstract class GenEnum2GenOneOfMany extends GenEditableEnumValueElem with GenEditableOneOfManyValueElem {

  protected case class EnumValue(v: EnumValueType) {
    def name = enumValue2String(v)

    def id = v.id + ""
  }

  protected def enumValue2String(v: EnumValueType): String = v.toString

  protected type OneOfManyValue = EnumValue

  def getOneOfManyValue() = EnumValue(getEnumValue())

  def getCurrentEnumValue() = getCurrentOneOfManyValue().v

  def getAllOneOfManyValues() = enum.values.map(EnumValue(_)).toSeq.sortBy(_.v.id)
}

abstract class GenSeq2GenOneOfMany extends GenEditableSeqValueElem with GenEditableOneOfManyValueElem {

  protected case class SeqValue(v: SeqValueType, idx: Int) {
    def name = seqValue2String(v)

    def id = idx + ""
  }

  protected def seqValue2String(v: SeqValueType): String = v.toString

  protected type OneOfManyValue = SeqValue

  def getOneOfManyValue() = SeqValue(getSeqValue(), seq.indexOf(getSeqValue()))

  def getCurrentSeqValue() = getCurrentOneOfManyValue().v

  def getAllOneOfManyValues() = seq.zipWithIndex.map(e => SeqValue(e._1, e._2)).sortBy(_.name)
}

trait PasswordInputElem extends TextInputElem {

  override protected def inputElem: NodeSeq = ("input [type]" #> "password").apply(super.inputElem)
}

trait TextInputElem extends GenEditableStringValueElem with HTMLEditableElem with NamedElem {

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
      ("onblur" -> SHtml.onEvent(v => {value = v; onChangeServerSide()}).toJsCmd)
    ): _*)

  override protected def htmlEditableElemRendererTransforms =
    super.htmlEditableElemRendererTransforms andThen (
      ".elem-wrap [style+]" #> (if (!enabled()) "display:none;" else "") &
        ".elem-wrap [id]" #> id('wrapper) &
        ".elem-lbl *" #> wrapName(S.?(s"elem-lbl-$elemName")) &
        ".elem-error [id]" #> id('error)
      ) andThen
      ((ns: NodeSeq) => bind("elem", ns, "input" -%> inputElem))
}

trait SelectInputElem extends GenOneOfManyValueElem with HTMLEditableElem with NamedElem {

  private var value = getOneOfManyValue()

  def getCurrentOneOfManyValue() = value

  protected def selectInputAttrs: Seq[ElemAttr]

  override protected def htmlEditableElemRendererTransforms =
    super.htmlEditableElemRendererTransforms andThen (
      ".elem-wrap [style+]" #> (if (!enabled()) "display:none;" else "") &
        ".elem-wrap [id]" #> id('wrapper) &
        ".elem-lbl *" #> wrapName(S.?(s"elem-lbl-$elemName")) &
        ".elem-error [id]" #> id('error)
      ) andThen
      ((ns: NodeSeq) => bind("elem", ns, "input" -%>
        SHtml.select(
          getAllOneOfManyValues().map(v => (v.id, v.name)), Full(value.id),
          v => value = getAllOneOfManyValues().find(_.id == v).get,
          (selectInputAttrs ++ Seq[ElemAttr](
            ("id" -> id('input)),
            ("onchange" -> SHtml.onEvent(v => {value = getAllOneOfManyValues().find(_.id == v).get; onChangeServerSide()}).toJsCmd)
          )): _*)
      ))
}

trait CheckboxInputElem extends GenEditableBooleanValueElem with HTMLEditableElem with NamedElem {

  override protected def wrapName(name: String) = name

  private var value = getBooleanValue()

  def getCurrentBooleanValue() = value

  protected def checkboxInputAttrs: Seq[ElemAttr]

  override protected def htmlEditableElemRendererTransforms =
    super.htmlEditableElemRendererTransforms andThen (
      ".elem-wrap [style+]" #> (if (!enabled()) "display:none;" else "") &
        ".elem-wrap [id]" #> id('wrapper) &
        ".elem-lbl *" #> wrapName(S.?(s"elem-lbl-$elemName")) &
        ".elem-error [id]" #> id('error)
      ) andThen
      ((ns: NodeSeq) => bind("elem", ns, "input" -%>
        SHtml.checkbox(value, value = _,
          (checkboxInputAttrs ++ Seq[ElemAttr](
            ("id" -> id('input)),
            ("onchange" -> SHtml.ajaxCall(JsRaw(sel('input) + ".is(':checked')"),
              v => {value = v.toBoolean; onChangeServerSide()}).toJsCmd)
          )): _*)
      ))
}

trait IconElem extends HTMLEditableElem {def icon: String}

trait HTMLIIconElem extends IconElem {
  override def htmlEditableElemRendererTransforms =
    super.htmlEditableElemRendererTransforms andThen
      "i [class]" #> s"icon-$icon"
}

object Validation {

  trait ReqString extends GenEditableStringValueElem {

    override private[elem] def error: Option[NodeSeq] =
      super.error.map(Some(_)).getOrElse(
        if (getCurrentStringValue().isEmpty) Some(Text("Value is required")) else None)
  }

  trait Hostname extends ReqString {

    import scala.concurrent._
    import ExecutionContext.Implicits.global

    override private[elem] def error: Option[NodeSeq] =
      super.error.map(Some(_)).getOrElse(
        (tryo(
          Await.result(
            future(InetAddress.getByName(getCurrentStringValue())),
            Duration(3, scala.concurrent.duration.SECONDS)))
          .map(_ => None).getOrElse(Some(Text("Invalid hostname")))))
  }

  trait Email extends ReqString {
    val ptr = Pattern.compile(
      "(?:(?:\\r\\n)?[ \\t])*(?:(?:(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*\\<(?:(?:\\r\\n)?[ \\t])*(?:@(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*(?:,@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*)*:(?:(?:\\r\\n)?[ \\t])*)?(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*\\>(?:(?:\\r\\n)?[ \\t])*)|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*:(?:(?:\\r\\n)?[ \\t])*(?:(?:(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*\\<(?:(?:\\r\\n)?[ \\t])*(?:@(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*(?:,@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*)*:(?:(?:\\r\\n)?[ \\t])*)?(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*\\>(?:(?:\\r\\n)?[ \\t])*)(?:,\\s*(?:(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*|(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)*\\<(?:(?:\\r\\n)?[ \\t])*(?:@(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*(?:,@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*)*:(?:(?:\\r\\n)?[ \\t])*)?(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\"(?:[^\\\"\\r\\\\]|\\\\.|(?:(?:\\r\\n)?[ \\t]))*\"(?:(?:\\r\\n)?[ \\t])*))*@(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*)(?:\\.(?:(?:\\r\\n)?[ \\t])*(?:[^()<>@,;:\\\\\".\\[\\] \\000-\\031]+(?:(?:(?:\\r\\n)?[ \\t])+|\\Z|(?=[\\[\"()<>@,;:\\\\\".\\[\\]]))|\\[([^\\[\\]\\r\\\\]|\\\\.)*\\](?:(?:\\r\\n)?[ \\t])*))*\\>(?:(?:\\r\\n)?[ \\t])*))*)?;\\s*)",
      Pattern.CASE_INSENSITIVE)

    override private[elem] def error: Option[NodeSeq] =
      super.error.map(Some(_)).getOrElse(
        if (ptr.matcher(getCurrentStringValue()).matches()) None else Some(Text("Invalid hostname")))
  }

}
