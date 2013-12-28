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
  protected type OneOfManyValue <: Object {def name: NodeSeq; def id: String}

  def getOneOfManyValue(): OneOfManyValue

  def getAllOneOfManyValues(): Seq[OneOfManyValue]
}

trait GenEditableOneOfManyValueElem extends GenOneOfManyValueElem with ValidatableElem {def getCurrentOneOfManyValue(): OneOfManyValue}

abstract class GenDouble2GenString extends GenEditableDoubleValueElem with GenEditableStringValueElem {

  protected def double2StringFormat = "%f"

  override def error: Option[NodeSeq] =
    Try(getCurrentStringValue().toDouble).map(_ => super.error).getOrElse(Some(Text("Invalid value.")))

  def getCurrentDoubleValue(): Double = getCurrentStringValue().toDouble

  def getStringValue() = double2StringFormat.format(getDoubleValue())
}

abstract class GenEnum2GenOneOfMany extends GenEditableEnumValueElem with GenEditableOneOfManyValueElem {

  protected case class EnumValue(v: EnumValueType) {
    def name = enumValue2NodeSeq(v)

    def id = Option(v).map(_.id + "").getOrElse("")
  }

  protected def enumValue2NodeSeq(v: EnumValueType): NodeSeq

  protected type OneOfManyValue = EnumValue

  def getOneOfManyValue() = EnumValue(getEnumValue())

  def getCurrentEnumValue() = getCurrentOneOfManyValue().v

  def getAllOneOfManyValues() = enum.values.map(EnumValue(_)).toSeq.sortBy(_.v.id)
}

abstract class GenSeq2GenOneOfMany extends GenEditableSeqValueElem with GenEditableOneOfManyValueElem {

  protected case class SeqValue(v: SeqValueType, idx: Int) {
    def name = seqValue2NodeSeq(v)

    def id = idx + ""
  }

  protected def seqValue2NodeSeq(v: SeqValueType): NodeSeq

  protected type OneOfManyValue = SeqValue

  def getOneOfManyValue() = SeqValue(getSeqValue(), seq.indexOf(getSeqValue()))

  def getCurrentSeqValue() = getCurrentOneOfManyValue().v

  def getAllOneOfManyValues() = seq.zipWithIndex.map(e => SeqValue(e._1, e._2))
}
