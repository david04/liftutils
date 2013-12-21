package com.github.david04.liftutils.elem

import net.liftweb.http.SHtml.ElemAttr

trait Bootstrap3 extends Framework {

  def fw: Framework = this

  def errorClass = "has-error"
  def warningClass = "has-warning"
  def successClass = "has-success"

  def btnDefault: String = "btn-default"
  def btnMute: String = "btn-default"
  def btnPrimary: String = "btn-primary"
  def btnSuccess: String = "btn-success"
  def btnInfo: String = "btn-info"
  def btnWarning: String = "btn-warning"
  def btnDanger: String = "btn-danger"
}

trait Bootstrap2 extends Framework {

  def fw: Framework = this

  def errorClass = "error"
  def warningClass: String = ???
  def successClass: String = ???

  def btnDefault: String = ???
  def btnMute: String = ???
  def btnPrimary: String = ???
  def btnSuccess: String = ???
  def btnInfo: String = ???
  def btnWarning: String = ???
  def btnDanger: String = ???
}

trait DefaultElems {

  class Text(
              val elemName: String,
              get: => String,
              set: String => Unit,
              protected val placeholder: Option[String] = None,
              private[elem] val enabled: () => Boolean = () => true,
              protected val textInputAttrs: Seq[ElemAttr] = Seq()
              )(implicit protected val editor: DefaultHTMLEditor) extends TextInputElem with EditableElem2DefaultEditorBridge {

    def getStringValue(): String = get

    private[elem] def save(): Unit = set(getCurrentStringValue())
  }

  class Password(
                  val elemName: String,
                  get: => String,
                  set: String => Unit,
                  protected val placeholder: Option[String],
                  private[elem] val enabled: () => Boolean = () => true,
                  protected val textInputAttrs: Seq[ElemAttr] = Seq()
                  )(implicit protected val editor: DefaultHTMLEditor) extends PasswordInputElem with EditableElem2DefaultEditorBridge {

    def getStringValue(): String = get

    private[elem] def save(): Unit = set(getCurrentStringValue())
  }

  class Real(
              val elemName: String,
              get: => Double,
              set: Double => Unit,
              protected val placeholder: Option[String] = None,
              private[elem] val enabled: () => Boolean = () => true,
              protected val textInputAttrs: Seq[ElemAttr] = Seq()
              )(implicit protected val editor: DefaultHTMLEditor) extends GenDouble2GenString with TextInputElem with EditableElem2DefaultEditorBridge {


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

    protected def errorClass = framework.errorClass

    protected def seq: Seq[SeqValueType] = all

    def getSeqValue() = get

    private[elem] def save(): Unit = set(getCurrentSeqValue())
  }

  class Tree(
              val elemName: String,
              _get: => String,
              val set: String => Unit,
              _all: => Array[String],
              private[elem] val enabled: () => Boolean = () => true
              )(implicit protected val editor: DefaultHTMLEditor) extends FuelUXTree with EditableElem2DefaultEditorBridge {
    def get = () => _get

    def all: Array[String] = _all

  }

}

object DefaultEditableElems extends DefaultElems