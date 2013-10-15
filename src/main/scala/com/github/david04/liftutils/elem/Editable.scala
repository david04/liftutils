package com.github.david04.liftutils.elem

import scala.xml.NodeSeq
import scala.xml.Text
import net.liftweb.common._
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

trait ID {
  private val _id = UUID.randomUUID().toString

  def id(part: Symbol) = _id + "-" + part.name

  def sel(part: Symbol) = "$('#" + id(part) + "')"
}

trait Elem extends ID {}

trait Framework {

  def errorClass: String

  def warningClass: String

  def successClass: String
}

trait ValidatableElem extends Elem {
  private[elem] def error: Option[NodeSeq] = None
}

trait EditableElem extends ValidatableElem {

  protected def onChangeServerSide(): JsCmd

  protected def wrapName(name: String) = name + ": "

  protected def framework: Framework

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

  protected def templateLoc: List[String] = "templates-hidden" :: "elem-edit-dflt" :: Nil

  protected lazy val template = Templates(templateLoc).get

  private[elem] val enabled: () => Boolean

  private[elem] def save(): Unit

  private[elem] def edit: NodeSeq
}

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

trait GenEditableEnumValueElem extends GenEnumValueElem with ValidatableElem {def getCurrentEnumValue(): EnumValueType}

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

trait TextInputElem extends GenEditableStringValueElem with EditableElem {

  protected def name: String

  protected def placeholder: Option[String]

  private var value: String = getStringValue()

  def getCurrentStringValue(): String = value

  protected def textInputAttrs: Seq[ElemAttr]

  import ElemAttr._

  private[elem] def edit = {
    bind("elem", (
      ".elem-wrap [style+]" #> (if (!enabled()) "display:none;" else "") &
        ".elem-wrap [id]" #> id('wrapper) &
        ".elem-lbl *" #> wrapName(name) &
        ".elem-error [id]" #> id('error)
      )(template),
      "input" -%> SHtml.text(value, value = _,
        textInputAttrs ++ Seq[ElemAttr](
          ("id" -> id('input)),
          ("placeholder" -> placeholder.getOrElse("")),
          ("onblur" -> SHtml.onEvent(v => {value = v; onChangeServerSide()}).toJsCmd)
        ): _*))
  }
}

trait SelectInputElem extends GenOneOfManyValueElem with EditableElem {

  protected def name: String

  private var value = getOneOfManyValue()

  def getCurrentOneOfManyValue() = value

  protected def selectInputAttrs: Seq[ElemAttr]

  private[elem] def edit = {
    bind("elem", (
      ".elem-wrap [style+]" #> (if (!enabled()) "display:none;" else "") &
        ".elem-wrap [id]" #> id('wrapper) &
        ".elem-lbl *" #> wrapName(name) &
        ".elem-error [id]" #> id('error)
      )(template),
      "input" -%> SHtml.select(
        getAllOneOfManyValues().map(v => (v.id, v.name)), Full(value.id),
        v => getAllOneOfManyValues().find(_.id == v).get,
        (selectInputAttrs ++ Seq[ElemAttr](
          ("id" -> id('input)),
          ("onchange" -> SHtml.onEvent(v => {value = getAllOneOfManyValues().find(_.id == v).get; onChangeServerSide()}).toJsCmd)
        )): _*))
  }
}

trait CheckboxInputElem extends GenEditableBooleanValueElem with EditableElem {

  protected def name: String

  override protected def wrapName(name: String) = name

  private var value = getBooleanValue()

  def getCurrentBooleanValue() = value

  protected def checkboxInputAttrs: Seq[ElemAttr]

  private[elem] def edit = {
    bind("elem", (
      ".elem-wrap [style+]" #> (if (!enabled()) "display:none;" else "") &
        ".elem-wrap [id]" #> id('wrapper) &
        ".elem-lbl *" #> wrapName(name) &
        ".elem-error [id]" #> id('error)
      )(template),
      "input" -%> SHtml.checkbox(value, value = _,
        (checkboxInputAttrs ++ Seq[ElemAttr](
          ("id" -> id('input)),
          ("onchange" -> SHtml.ajaxCall(JsRaw(sel('input) + ".is(':checked')"),
            v => {value = v.toBoolean; onChangeServerSide()}).toJsCmd)
        )): _*))
  }
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
