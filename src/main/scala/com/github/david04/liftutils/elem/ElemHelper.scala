package com.github.david04.liftutils.elem

import net.liftweb.http.SHtml.ElemAttr

trait Bootstrap3 extends Framework {

  def fw: Framework = this

  def errorClass = "has-error"

  def warningClass = "has-warning"

  def successClass = "has-success"
}

trait Bootstrap2 extends Framework {

  def fw: Framework = this

  def errorClass = "error"

  def warningClass: String = ???

  def successClass: String = ???
}

trait EditableHelper {

  def fw: Framework

  class Text(
              val elemName: String,
              get: => String,
              set: String => Unit,
              protected val placeholder: Option[String],
              private[elem] val enabled: () => Boolean = () => true,
              protected val textInputAttrs: Seq[ElemAttr] = Seq()
              )(implicit protected val editor: HTMLEditor) extends TextInputElem with EditableElem2EditorBridge {

    protected def framework = fw

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
                  )(implicit protected val editor: HTMLEditor) extends PasswordInputElem with EditableElem2EditorBridge {

    protected def framework = fw

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
              )(implicit protected val editor: HTMLEditor) extends GenDouble2GenString with TextInputElem with EditableElem2EditorBridge {

    protected def framework = fw

    def getDoubleValue(): Double = get

    private[elem] def save(): Unit = set(getCurrentDoubleValue())
  }

  class Bool(
              val elemName: String,
              get: => Boolean,
              set: Boolean => Unit,
              private[elem] val enabled: () => Boolean = () => true,
              protected val checkboxInputAttrs: Seq[ElemAttr] = Seq()
              )(implicit protected val editor: HTMLEditor) extends GenEditableBooleanValueElem with CheckboxInputElem with EditableElem2EditorBridge {

    protected def framework = fw

    def getBooleanValue(): Boolean = get

    private[elem] def save(): Unit = set(getCurrentBooleanValue())
  }

  class Enum[E <: Enumeration](
                                val elemName: String,
                                get: => E#Value,
                                set: E#Value => Unit,
                                protected val enum: E,
                                private[elem] val enabled: () => Boolean = () => true,
                                protected val selectInputAttrs: Seq[ElemAttr] = Seq())(implicit protected val editor: HTMLEditor) extends GenEnum2GenOneOfMany with SelectInputElem with EditableElem2EditorBridge {

    protected type EnumType = E

    protected def framework = fw

    protected def errorClass = fw.errorClass

    def getEnumValue: E#Value = get

    private[elem] def save(): Unit = set(getCurrentEnumValue())
  }

}

object BS2EditableHelper extends EditableHelper with Bootstrap2

object BS3EditableHelper extends EditableHelper with Bootstrap3