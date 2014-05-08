//  Copyright (c) 2014 David Miguel Antunes <davidmiguel {at} antunes.net>
//
//  Permission is hereby granted, free of charge, to any person obtaining a copy
//  of this software and associated documentation files (the "Software"), to deal
//  in the Software without restriction, including without limitation the rights
//  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
//  copies of the Software, and to permit persons to whom the Software is
//  furnished to do so, subject to the following conditions:
//
//  The above copyright notice and this permission notice shall be included in
//  all copies or substantial portions of the Software.
//
//  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
//  THE SOFTWARE.

package com.github.david04.liftutils.elem

import net.liftweb.http.SHtml.ElemAttr
import scala.xml.NodeSeq
import net.liftweb.http.{SHtml, S}
import com.github.david04.liftutils.loc.Loc
import net.liftweb.http.js.JsCmds.{Run, OnLoad, Script}
import net.liftweb.common.Loggable
import java.util.{Date, TimeZone}
import net.liftweb.http.js.JsCmd


trait DefaultElems extends Loggable {

  class Text(
              val elemName: String,
              get: => String,
              set: String => JsCmd,
              val enabled: () => Boolean = () => true,
              protected val textInputAttrs: Seq[ElemAttr] = Seq()
              )(implicit protected val editor: DefaultHTMLEditor) extends TextInputElem with EditableElem2DefaultEditorBridge {

    protected val placeholder: Option[String] = labelStrOpt("placeholder")

    def getStringValue(): String = get

    private[elem] def save(): JsCmd = set(getCurrentStringValue())
  }

  class TextArea(
                  val elemName: String,
                  get: => String,
                  set: String => JsCmd,
                  val enabled: () => Boolean = () => true,
                  protected val textInputAttrs: Seq[ElemAttr] = Seq()
                  )(implicit protected val editor: DefaultHTMLEditor) extends TextAreaInputElem with EditableElem2DefaultEditorBridge {

    protected val placeholder: Option[String] = labelStrOpt("placeholder")

    def getStringValue(): String = get

    private[elem] def save(): JsCmd = set(getCurrentStringValue())
  }

  class Timezone(
                  val elemName: String,
                  set: TimeZone => JsCmd,
                  val enabled: () => Boolean = () => true
                  )(implicit protected val editor: DefaultHTMLEditor) extends HTMLEditableElem with LabeledElem with EditableElem2DefaultEditorBridge {

    //    protected def classes: List[String] = Nil

    protected var value: TimeZone = TimeZone.getTimeZone("UTC")

    protected val timezones =
      TimeZone.getAvailableIDs.map(TimeZone.getTimeZone(_))
        .map(tz => (tz.getOffset(new Date().getTime()) / 60 / 1000, tz))
        .toMap.withDefaultValue(TimeZone.getTimeZone("UTC"))

    override protected def htmlElemRendererTransforms: NodeSeq => NodeSeq =
      (_: NodeSeq) =>
        SHtml.hidden((s: String) => {
          try {value = timezones(s.toInt)} catch {case e: Exception => logger.error("Could not get timezone")}
        }, "null", Seq[ElemAttr]("id" -> id('elem)): _*) ++
          <tail>
            {Script(OnLoad(Run(sel('elem, ".val((new Date().getTimezoneOffset()) + '');"))))}
          </tail>

    private[elem] def save(): JsCmd = set(value)
  }

  class Formula(
                 val elemName: String,
                 get: => String,
                 set: String => JsCmd,
                 val enabled: () => Boolean = () => true,
                 protected val textInputAttrs: Seq[ElemAttr] = Seq()
                 )(implicit protected val editor: DefaultHTMLEditor) extends TextInputElem with EditableElem2DefaultEditorBridge {

    protected val placeholder: Option[String] = labelStrOpt("placeholder")

    def getStringValue(): String = get

    private[elem] def save(): JsCmd = set(getCurrentStringValue())
  }

  class Password(
                  val elemName: String,
                  get: => String,
                  set: String => JsCmd,
                  val enabled: () => Boolean = () => true,
                  protected val textInputAttrs: Seq[ElemAttr] = Seq()
                  )(implicit protected val editor: DefaultHTMLEditor) extends PasswordInputElem with EditableElem2DefaultEditorBridge with Loc {

    protected val placeholder: Option[String] = labelStrOpt("placeholder")

    def getStringValue(): String = get

    private[elem] def save(): JsCmd = set(getCurrentStringValue())
  }

  class Integer(
                 val elemName: String,
                 get: => Int,
                 set: Int => JsCmd,
                 val enabled: () => Boolean = () => true,
                 val suffix: Option[String] = None,
                 protected val textInputAttrs: Seq[ElemAttr] = Seq()
                 )(implicit protected val editor: DefaultHTMLEditor) extends GenInt2GenString with TextInputElem with EditableElem2DefaultEditorBridge {

    protected val placeholder: Option[String] = labelStrOpt("placeholder")

    def getIntValue(): Int = get

    private[elem] def save(): JsCmd = set(getCurrentIntValue())
  }

  class Real(
              val elemName: String,
              get: => Double,
              set: Double => JsCmd,
              val enabled: () => Boolean = () => true,
              val suffix: Option[String] = None,
              val precision: Int = 2,
              protected val textInputAttrs: Seq[ElemAttr] = Seq()
              )(implicit protected val editor: DefaultHTMLEditor) extends GenDouble2GenString with TextInputElem with EditableElem2DefaultEditorBridge {

    protected val placeholder: Option[String] = labelStrOpt("placeholder")

    def getDoubleValue(): Double = get

    private[elem] def save(): JsCmd = set(getCurrentDoubleValue())
  }

  class Bool(
              val elemName: String,
              get: => Boolean,
              set: Boolean => JsCmd,
              val enabled: () => Boolean = () => true,
              protected val checkboxInputAttrs: Seq[ElemAttr] = Seq()
              )(implicit protected val editor: DefaultHTMLEditor) extends GenEditableBooleanValueElem with CheckboxInputElem with EditableElem2DefaultEditorBridge {

    def getBooleanValue(): Boolean = get

    private[elem] def save(): JsCmd = set(getCurrentBooleanValue())
  }

  class Enum[E <: Enumeration](
                                val elemName: String,
                                get: => E#Value,
                                set: E#Value => JsCmd,
                                protected val enum: E,
                                val enabled: () => Boolean = () => true,
                                protected val selectInputAttrs: Seq[ElemAttr] = Seq())(implicit protected val editor: DefaultHTMLEditor) extends GenOneOfEnum2GenOneOfMany with SelectInputElem with EditableElem2DefaultEditorBridge {

    protected def enumValue2NodeSeq(v: EnumValueType): NodeSeq = scala.xml.Text(S.?(labelStr(v.toString)))

    protected type EnumType = E

    protected def errorClass = framework.errorClass

    def getOneOfEnumValue: E#Value = get

    private[elem] def save(): JsCmd = set(getCurrentOneOfEnumValue())
  }

  class MultiEnum[E <: Enumeration](
                                     val elemName: String,
                                     get: => Seq[E#Value],
                                     set: Seq[E#Value] => JsCmd,
                                     protected val enum: E,
                                     val enabled: () => Boolean = () => true,
                                     protected val selectInputAttrs: Seq[ElemAttr] = Seq())(implicit protected val editor: DefaultHTMLEditor) extends GenManyOfEnum2GenOneOfMany with MultiSelectInputElem with EditableElem2DefaultEditorBridge {

    protected def enumValue2NodeSeq(v: EnumValueType): NodeSeq = scala.xml.Text(S.?(labelStr(v.toString)))

    protected type EnumType = E

    protected def errorClass = framework.errorClass

    def getManyOfEnumValue: Seq[E#Value] = get

    private[elem] def save(): JsCmd = set(getCurrentManyOfEnumValue())
  }

  class Select[T](
                   val elemName: String,
                   get: => T,
                   set: T => JsCmd,
                   all: => Seq[T],
                   val enabled: () => Boolean = () => true,
                   protected val selectInputAttrs: Seq[ElemAttr] = Seq())(implicit protected val editor: DefaultHTMLEditor) extends GenOneOfSeq2GenOneOfMany with SelectInputElem with EditableElem2DefaultEditorBridge {

    type SeqValueType = T

    protected def seqValue2NodeSeq(v: SeqValueType): NodeSeq = scala.xml.Text(labelStr(v.toString))

    protected def errorClass = framework.errorClass

    protected def seq: Seq[SeqValueType] = all

    def getOneOfSeqValue() = get

    private[elem] def save(): JsCmd = set(getCurrentOneOfSeqValue())
  }

  class MultiSelect[T](
                        val elemName: String,
                        get: => Seq[T],
                        set: Seq[T] => JsCmd,
                        all: => Seq[T],
                        val enabled: () => Boolean = () => true,
                        protected val selectInputAttrs: Seq[ElemAttr] = Seq())(implicit protected val editor: DefaultHTMLEditor) extends GenManyOfSeq2GenManyOfMany with MultiSelectInputElem with EditableElem2DefaultEditorBridge {

    type SeqValueType = T

    protected def seqValue2NodeSeq(v: SeqValueType): NodeSeq = scala.xml.Text(labelStr(v.toString))

    protected def errorClass = framework.errorClass

    protected def seq: Seq[SeqValueType] = all

    def getManyOfSeqValue() = get

    private[elem] def save(): JsCmd = set(getCurrentManyOfSeqValue())
  }

  class Tree(
              val elemName: String,
              _get: => Option[String],
              val set: Option[String] => JsCmd,
              _all: => Seq[String],
              val enabled: () => Boolean = () => true,
              protected val allowSelectStar: Boolean = false
              )(implicit protected val editor: DefaultHTMLEditor) extends FuelUXTree with EditableElem2DefaultEditorBridge {
    def get = () => _get

    def all: Seq[String] = _all

  }

  class DateTimePicker(
                        val elemName: String,
                        _get: => (Long, Long),
                        val set: ((Long, Long)) => JsCmd,
                        val enabled: () => Boolean = () => true
                        )(implicit protected val editor: DefaultHTMLEditor) extends DateTimePickerInput with EditableElem2DefaultEditorBridge {
    def get = () => _get
  }

  class FileUpload(
                    val elemName: String,
                    val set: Option[(Array[Byte], String)] => JsCmd,
                    val enabled: () => Boolean = () => true,
                    protected val fileInputAttrs: Seq[ElemAttr] = Seq()
                    )(implicit protected val editor: DefaultHTMLEditor) extends FileUploadInputElem with EditableElem2DefaultEditorBridge {

    private[elem] def save(): JsCmd = set(getFile())
  }
}

object DefaultEditableElems extends DefaultElems
