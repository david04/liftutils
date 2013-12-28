package com.github.david04.liftutils.elem

import net.liftweb.http.SHtml.ElemAttr
import scala.xml.NodeSeq
import net.liftweb.http.S
import scala.util.parsing.combinator.RegexParsers
import com.github.david04.liftutils.Loc.Loc


trait DefaultElems {

  class Text(
              val elemName: String,
              get: => String,
              set: String => Unit,
              private[elem] val enabled: () => Boolean = () => true,
              protected val textInputAttrs: Seq[ElemAttr] = Seq()
              )(implicit protected val editor: DefaultHTMLEditor) extends TextInputElem with EditableElem2DefaultEditorBridge {

    protected val placeholder: Option[String] = labelStrOpt("placeholder")

    def getStringValue(): String = get

    private[elem] def save(): Unit = set(getCurrentStringValue())
  }

  class Formula(
                 val elemName: String,
                 get: => String,
                 set: String => Unit,
                 private[elem] val enabled: () => Boolean = () => true,
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
                  private[elem] val enabled: () => Boolean = () => true,
                  protected val textInputAttrs: Seq[ElemAttr] = Seq()
                  )(implicit protected val editor: DefaultHTMLEditor) extends PasswordInputElem with EditableElem2DefaultEditorBridge with Loc {

    protected val placeholder: Option[String] = labelStrOpt("placeholder")

    def getStringValue(): String = get

    private[elem] def save(): Unit = set(getCurrentStringValue())
  }

  class Real(
              val elemName: String,
              get: => Double,
              set: Double => Unit,
              private[elem] val enabled: () => Boolean = () => true,
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
              private[elem] val enabled: () => Boolean = () => true,
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
                                private[elem] val enabled: () => Boolean = () => true,
                                protected val selectInputAttrs: Seq[ElemAttr] = Seq())(implicit protected val editor: DefaultHTMLEditor) extends GenEnum2GenOneOfMany with SelectInputElem with EditableElem2DefaultEditorBridge {

    protected def enumValue2NodeSeq(v: EnumValueType): NodeSeq = scala.xml.Text(S.?(labelStr(v.toString)))

    protected type EnumType = E

    protected def errorClass = framework.errorClass

    def getEnumValue: E#Value = get

    private[elem] def save(): Unit = set(getCurrentEnumValue())
  }

  class Select[T](
                   val elemName: String,
                   get: => T,
                   set: T => Unit,
                   all: => Seq[T],
                   private[elem] val enabled: () => Boolean = () => true,
                   protected val selectInputAttrs: Seq[ElemAttr] = Seq())(implicit protected val editor: DefaultHTMLEditor) extends GenSeq2GenOneOfMany with SelectInputElem with EditableElem2DefaultEditorBridge {

    type SeqValueType = T

    protected def seqValue2NodeSeq(v: SeqValueType): NodeSeq = scala.xml.Text(labelStr(v.toString))

    protected def errorClass = framework.errorClass

    protected def seq: Seq[SeqValueType] = all

    def getSeqValue() = get

    private[elem] def save(): Unit = set(getCurrentSeqValue())
  }

  class Tree(
              val elemName: String,
              _get: => Option[String],
              val set: Option[String] => Unit,
              _all: => Array[String],
              private[elem] val enabled: () => Boolean = () => true,
              protected val allowSelectStar: Boolean = true
              )(implicit protected val editor: DefaultHTMLEditor) extends FuelUXTree with EditableElem2DefaultEditorBridge {
    def get = () => _get

    def all: Array[String] = _all

  }

}

object DefaultEditableElems extends DefaultElems