package com.github.david04.liftutils.elem

import net.liftweb.http.SHtml.ElemAttr
import scala.xml.NodeSeq
import net.liftweb.http.{SHtml, S}
import scala.util.parsing.combinator.RegexParsers
import com.github.david04.liftutils.Loc.Loc
import net.liftweb.http.js.JsCmds.{Run, OnLoad, Script}
import net.liftweb.common.Loggable
import java.util.{Date, TimeZone}


trait DefaultElems extends Loggable {

  class Text(
              val elemName: String,
              get: => String,
              set: String => Unit,
              val enabled: () => Boolean = () => true,
              protected val textInputAttrs: Seq[ElemAttr] = Seq()
              )(implicit protected val editor: DefaultHTMLEditor) extends TextInputElem with EditableElem2DefaultEditorBridge {

    protected val placeholder: Option[String] = labelStrOpt("placeholder")

    def getStringValue(): String = get

    private[elem] def save(): Unit = set(getCurrentStringValue())
  }

  class TextArea(
              val elemName: String,
              get: => String,
              set: String => Unit,
              val enabled: () => Boolean = () => true,
              protected val textInputAttrs: Seq[ElemAttr] = Seq()
              )(implicit protected val editor: DefaultHTMLEditor) extends TextAreaInputElem with EditableElem2DefaultEditorBridge {

    protected val placeholder: Option[String] = labelStrOpt("placeholder")

    def getStringValue(): String = get

    private[elem] def save(): Unit = set(getCurrentStringValue())
  }

  class Timezone(
                  val elemName: String,
                  set: TimeZone => Unit,
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

    private[elem] def save(): Unit = set(value)
  }

  class Formula(
                 val elemName: String,
                 get: => String,
                 set: String => Unit,
                 val enabled: () => Boolean = () => true,
                 protected val textInputAttrs: Seq[ElemAttr] = Seq()
                 )(implicit protected val editor: DefaultHTMLEditor) extends TextInputElem with EditableElem2DefaultEditorBridge {

    protected val placeholder: Option[String] = labelStrOpt("placeholder")

    def getStringValue(): String = get

    private[elem] def save(): Unit = set(getCurrentStringValue())
  }

  class Password(
                  val elemName: String,
                  get: => String,
                  set: String => Unit,
                  val enabled: () => Boolean = () => true,
                  protected val textInputAttrs: Seq[ElemAttr] = Seq()
                  )(implicit protected val editor: DefaultHTMLEditor) extends PasswordInputElem with EditableElem2DefaultEditorBridge with Loc {

    protected val placeholder: Option[String] = labelStrOpt("placeholder")

    def getStringValue(): String = get

    private[elem] def save(): Unit = set(getCurrentStringValue())
  }

  class Integer(
                 val elemName: String,
                 get: => Int,
                 set: Int => Unit,
                 val enabled: () => Boolean = () => true,
                 val suffix: Option[String] = None,
                 protected val textInputAttrs: Seq[ElemAttr] = Seq()
                 )(implicit protected val editor: DefaultHTMLEditor) extends GenInt2GenString with TextInputElem with EditableElem2DefaultEditorBridge {

    protected val placeholder: Option[String] = labelStrOpt("placeholder")

    def getIntValue(): Int = get

    private[elem] def save(): Unit = set(getCurrentIntValue())
  }

  class Real(
              val elemName: String,
              get: => Double,
              set: Double => Unit,
              val enabled: () => Boolean = () => true,
              val suffix: Option[String] = None,
              val precision: Int = 2,
              protected val textInputAttrs: Seq[ElemAttr] = Seq()
              )(implicit protected val editor: DefaultHTMLEditor) extends GenDouble2GenString with TextInputElem with EditableElem2DefaultEditorBridge {

    protected val placeholder: Option[String] = labelStrOpt("placeholder")

    def getDoubleValue(): Double = get

    private[elem] def save(): Unit = set(getCurrentDoubleValue())
  }

  class Bool(
              val elemName: String,
              get: => Boolean,
              set: Boolean => Unit,
              val enabled: () => Boolean = () => true,
              protected val checkboxInputAttrs: Seq[ElemAttr] = Seq()
              )(implicit protected val editor: DefaultHTMLEditor) extends GenEditableBooleanValueElem with CheckboxInputElem with EditableElem2DefaultEditorBridge {

    def getBooleanValue(): Boolean = get

    private[elem] def save(): Unit = set(getCurrentBooleanValue())
  }

  class Enum[E <: Enumeration](
                                val elemName: String,
                                get: => E#Value,
                                set: E#Value => Unit,
                                protected val enum: E,
                                val enabled: () => Boolean = () => true,
                                protected val selectInputAttrs: Seq[ElemAttr] = Seq())(implicit protected val editor: DefaultHTMLEditor) extends GenOneOfEnum2GenOneOfMany with SelectInputElem with EditableElem2DefaultEditorBridge {

    protected def enumValue2NodeSeq(v: EnumValueType): NodeSeq = scala.xml.Text(S.?(labelStr(v.toString)))

    protected type EnumType = E

    protected def errorClass = framework.errorClass

    def getOneOfEnumValue: E#Value = get

    private[elem] def save(): Unit = set(getCurrentOneOfEnumValue())
  }

  class MultiEnum[E <: Enumeration](
                                     val elemName: String,
                                     get: => Seq[E#Value],
                                     set: Seq[E#Value] => Unit,
                                     protected val enum: E,
                                     val enabled: () => Boolean = () => true,
                                     protected val selectInputAttrs: Seq[ElemAttr] = Seq())(implicit protected val editor: DefaultHTMLEditor) extends GenManyOfEnum2GenOneOfMany with MultiSelectInputElem with EditableElem2DefaultEditorBridge {

    protected def enumValue2NodeSeq(v: EnumValueType): NodeSeq = scala.xml.Text(S.?(labelStr(v.toString)))

    protected type EnumType = E

    protected def errorClass = framework.errorClass

    def getManyOfEnumValue: Seq[E#Value] = get

    private[elem] def save(): Unit = set(getCurrentManyOfEnumValue())
  }

  class Select[T](
                   val elemName: String,
                   get: => T,
                   set: T => Unit,
                   all: => Seq[T],
                   val enabled: () => Boolean = () => true,
                   protected val selectInputAttrs: Seq[ElemAttr] = Seq())(implicit protected val editor: DefaultHTMLEditor) extends GenOneOfSeq2GenOneOfMany with SelectInputElem with EditableElem2DefaultEditorBridge {

    type SeqValueType = T

    protected def seqValue2NodeSeq(v: SeqValueType): NodeSeq = scala.xml.Text(labelStr(v.toString))

    protected def errorClass = framework.errorClass

    protected def seq: Seq[SeqValueType] = all

    def getOneOfSeqValue() = get

    private[elem] def save(): Unit = set(getCurrentOneOfSeqValue())
  }

  class MultiSelect[T](
                        val elemName: String,
                        get: => Seq[T],
                        set: Seq[T] => Unit,
                        all: => Seq[T],
                        val enabled: () => Boolean = () => true,
                        protected val selectInputAttrs: Seq[ElemAttr] = Seq())(implicit protected val editor: DefaultHTMLEditor) extends GenManyOfSeq2GenManyOfMany with MultiSelectInputElem with EditableElem2DefaultEditorBridge {

    type SeqValueType = T

    protected def seqValue2NodeSeq(v: SeqValueType): NodeSeq = scala.xml.Text(labelStr(v.toString))

    protected def errorClass = framework.errorClass

    protected def seq: Seq[SeqValueType] = all

    def getManyOfSeqValue() = get

    private[elem] def save(): Unit = set(getCurrentManyOfSeqValue())
  }

  class Tree(
              val elemName: String,
              _get: => Option[String],
              val set: Option[String] => Unit,
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
                        val set: ((Long, Long)) => Unit,
                        val enabled: () => Boolean = () => true
                        )(implicit protected val editor: DefaultHTMLEditor) extends DateTimePickerInput with EditableElem2DefaultEditorBridge {
    def get = () => _get
  }

}

object DefaultEditableElems extends DefaultElems