package com.github.david04.liftutils.elem


import scala.xml.NodeSeq
import scala.xml.Text
import net.liftweb.util.Helpers._
import scala.util.Try

// get***Value: in the server
// getCurrent***Value: in the client

trait GenDoubleValueElem extends Elem {def getDoubleValue(): Double}

trait GenEditableDoubleValueElem extends GenDoubleValueElem with ValidatableElem {def getCurrentDoubleValue(): Double}

trait GenIntValueElem extends Elem {def getIntValue(): Int}

trait GenEditableIntValueElem extends GenIntValueElem with ValidatableElem {def getCurrentIntValue(): Int}

trait GenBooleanValueElem extends Elem {def getBooleanValue(): Boolean}

trait GenEditableBooleanValueElem extends GenBooleanValueElem with ValidatableElem {def getCurrentBooleanValue(): Boolean}

trait GenStringValueElem extends Elem {def getStringValue(): String}

trait GenEditableStringValueElem extends GenStringValueElem with ValidatableElem {def getCurrentStringValue(): String}

trait GenDateTimeValueElem extends Elem {def getDateTimeValue(): (Long, Long)}

trait GenEditableDateTimeValueElem extends GenDateTimeValueElem with ValidatableElem {def getCurrentDateTimeValue(): (Long, Long)}

trait GenOneOfEnumValueElem extends Elem {
  protected type EnumType <: Enumeration
  protected type EnumValueType = EnumType#Value

  protected def enum: EnumType

  def getOneOfEnumValue(): EnumValueType
}

trait GenEditableOneOfEnumValueElem extends GenOneOfEnumValueElem with ValidatableElem {def getCurrentOneOfEnumValue(): EnumValueType}

trait GenManyOfEnumValueElem extends Elem {
  protected type EnumType <: Enumeration
  protected type EnumValueType = EnumType#Value

  protected def enum: EnumType

  def getManyOfEnumValue(): Seq[EnumValueType]
}

trait GenEditableManyOfEnumValueElem extends GenManyOfEnumValueElem with ValidatableElem {def getCurrentManyOfEnumValue(): Seq[EnumValueType]}

trait GenOneOfSeqValueElem extends Elem {
  protected type SeqValueType

  protected def seq: Seq[SeqValueType]

  def getOneOfSeqValue(): SeqValueType
}

trait GenEditableOneOfSeqValueElem extends GenOneOfSeqValueElem with ValidatableElem {def getCurrentOneOfSeqValue(): SeqValueType}

trait GenManyOfSeqValueElem extends Elem {
  protected type SeqValueType

  protected def seq: Seq[SeqValueType]

  def getManyOfSeqValue(): Seq[SeqValueType]
}

trait GenEditableManyOfSeqValueElem extends GenManyOfSeqValueElem with ValidatableElem {def getCurrentManyOfSeqValue(): Seq[SeqValueType]}

trait GenOneOfManyValueElem extends Elem {
  protected type OneOfManyValue <: Object {def name: NodeSeq; def id: String}

  def getOneOfManyValue(): OneOfManyValue

  def getAllOneOfManyValues(): Seq[OneOfManyValue]
}

trait GenEditableOneOfManyValueElem extends GenOneOfManyValueElem with ValidatableElem {def getCurrentOneOfManyValue(): OneOfManyValue}

trait GenManyOfManyValueElem extends Elem {
  protected type ManyOfManyValue <: Object {def name: NodeSeq; def id: String}

  def getManyOfManyValue(): Seq[ManyOfManyValue]

  def getAllManyOfManyValues(): Seq[ManyOfManyValue]
}

trait GenEditableManyOfManyValueElem extends GenManyOfManyValueElem with ValidatableElem {def getCurrentManyOfManyValue(): Seq[ManyOfManyValue]}

abstract class GenDouble2GenString extends GenEditableDoubleValueElem with GenEditableStringValueElem {

  val suffix: Option[String]
  val precision: Int

  protected def double2StringFormat = s"%.${precision}f"

  private def currentStringValueWithoutSuffix() =
    suffix.map(suffix => getCurrentStringValue().replaceAllLiterally(suffix, "")).getOrElse(getCurrentStringValue())

  override def error: Option[NodeSeq] =
    Try(currentStringValueWithoutSuffix().toDouble).map(_ => super.error).getOrElse(Some(Text("Invalid value.")))

  def getCurrentDoubleValue(): Double = currentStringValueWithoutSuffix().toDouble

  def getStringValue() = double2StringFormat.format(getDoubleValue()) + suffix.getOrElse("")
}

abstract class GenInt2GenString extends GenEditableIntValueElem with GenEditableStringValueElem {

  val suffix: Option[String]

  private def currentStringValueWithoutSuffix() =
    suffix.map(suffix => getCurrentStringValue().replaceAllLiterally(suffix, "")).getOrElse(getCurrentStringValue())

  override def error: Option[NodeSeq] =
    Try(currentStringValueWithoutSuffix().toInt).map(_ => super.error).getOrElse(Some(Text("Invalid value.")))

  def getCurrentIntValue(): Int = currentStringValueWithoutSuffix().toInt

  def getStringValue() = getIntValue().toString() + suffix.getOrElse("")
}

abstract class GenOneOfEnum2GenOneOfMany extends GenEditableOneOfEnumValueElem with GenEditableOneOfManyValueElem {

  protected case class EnumValue(v: EnumValueType) {
    def name = enumValue2NodeSeq(v)

    def id = Option(v).map(_.id + "").getOrElse("")
  }

  protected def enumValue2NodeSeq(v: EnumValueType): NodeSeq

  protected type OneOfManyValue = EnumValue

  def getOneOfManyValue() = EnumValue(getOneOfEnumValue())

  def getCurrentOneOfEnumValue() = getCurrentOneOfManyValue().v

  def getAllOneOfManyValues() = enum.values.map(EnumValue(_)).toSeq.sortBy(_.v.id)
}

abstract class GenManyOfEnum2GenOneOfMany extends GenEditableManyOfEnumValueElem with GenEditableManyOfManyValueElem {

  protected case class EnumValue(v: EnumValueType) {
    def name = enumValue2NodeSeq(v)

    def id = Option(v).map(_.id + "").getOrElse("")
  }

  protected def enumValue2NodeSeq(v: EnumValueType): NodeSeq

  protected type ManyOfManyValue = EnumValue

  def getManyOfManyValue() = getManyOfEnumValue().map(EnumValue(_))

  def getCurrentManyOfEnumValue() = getCurrentManyOfManyValue().map(_.v)

  def getAllManyOfManyValues() = enum.values.map(EnumValue(_)).toSeq.sortBy(_.v.id)
}

abstract class GenOneOfSeq2GenOneOfMany extends GenEditableOneOfSeqValueElem with GenEditableOneOfManyValueElem {

  protected case class SeqValue(v: SeqValueType, idx: Int) {
    def name = seqValue2NodeSeq(v)

    def id = idx + ""
  }

  protected def seqValue2NodeSeq(v: SeqValueType): NodeSeq

  protected type OneOfManyValue = SeqValue

  def getOneOfManyValue() = SeqValue(getOneOfSeqValue(), seq.indexOf(getOneOfSeqValue()))

  def getCurrentOneOfSeqValue() = getCurrentOneOfManyValue().v

  def getAllOneOfManyValues() = seq.zipWithIndex.map(e => SeqValue(e._1, e._2))
}

abstract class GenManyOfSeq2GenManyOfMany extends GenEditableManyOfSeqValueElem with GenEditableManyOfManyValueElem {

  protected case class SeqValue(v: SeqValueType, idx: Int) {
    def name = seqValue2NodeSeq(v)

    def id = idx + ""
  }

  protected def seqValue2NodeSeq(v: SeqValueType): NodeSeq

  protected type ManyOfManyValue = SeqValue

  def getManyOfManyValue() = getManyOfSeqValue().map(v => SeqValue(v, seq.indexOf(v)))

  def getCurrentManyOfSeqValue() = getCurrentManyOfManyValue().map(_.v)

  def getAllManyOfManyValues() = seq.zipWithIndex.map(e => SeqValue(e._1, e._2))
}
