package com.github.david04.liftutils.modtbl

import scala.xml.NodeSeq
import net.liftweb.util.Helpers._
import com.github.david04.liftutils.Loc.Loc

trait NamedColTable extends Table {
  type C <: NamedCol

  trait NamedCol extends TableCol {
    self: C =>
    def name: String

    override def renderHead: NodeSeq => NodeSeq = super.renderHead andThen "th [col-name]" #> name

    override def renderRow(row: R, rowId: String, idx: Int, colId: String): NodeSeq => NodeSeq =
      super.renderRow(row, rowId, idx, colId) andThen "td [col-name]" #> name
  }

}

trait NodeSeqHeadTable extends Table {
  type C <: NodeSeqHeadCol

  trait NodeSeqHeadCol extends TableCol {
    self: C =>
    def headNodeSeqValue(): NodeSeq

    def nodeSeqHeadTableTransforms() =
      "th *" #> headNodeSeqValue()

    override def renderHead: NodeSeq => NodeSeq = super.renderHead andThen nodeSeqHeadTableTransforms()
  }

}

trait CachedNodeSeqHeadTable extends NodeSeqHeadTable {
  type C <: CachedNodeSeqHeadCol

  trait CachedNodeSeqHeadCol extends NodeSeqHeadCol {
    self: C =>

    protected lazy val nodeSeqHeadCache = headNodeSeqValue()

    override def nodeSeqHeadTableTransforms() = "th *" #> nodeSeqHeadCache
  }

}

trait StrHeadTable extends NodeSeqHeadTable {
  type C <: StrHeadCol

  trait StrHeadCol extends NodeSeqHeadCol {
    self: C =>
    def title: String

    def headNodeSeqValue: NodeSeq = scala.xml.Text(title)
  }

}

trait LocStrHeadTable extends StrHeadTable with NamedColTable with Loc {
  type C <: LocStrHeadCol

  trait LocStrHeadCol extends StrHeadCol with NamedCol {
    self: C =>

    def title = loc(name + "-title")
  }

}

trait NodeSeqRowTable extends Table {

  trait NodeSeqRowCol extends TableCol {
    self: C =>
    def rowNodeSeqValue: R => NodeSeq

    def nodeSeqRowTableTransforms(row: R) = "td *" #> rowNodeSeqValue(row)

    override def renderRow(row: R, rowId: String, rowIdx: Int, colId: String): NodeSeq => NodeSeq =
      super.renderRow(row, rowId, rowIdx, colId) andThen
        nodeSeqRowTableTransforms(row)
  }

}

trait CachedNodeSeqRowTable extends NodeSeqRowTable {
  type C <: CachedNodeSeqRowCol

  trait CachedNodeSeqRowCol extends NodeSeqRowCol {
    self: C =>

    protected val nodeSeqRowCache = collection.mutable.Map[R, NodeSeq]()

    override def nodeSeqRowTableTransforms(row: R) =
      "td *" #> nodeSeqRowCache.getOrElseUpdate(row, rowNodeSeqValue(row))
  }

}

trait StrRowTable extends NodeSeqRowTable {

  trait StrRowCol extends NodeSeqRowCol {
    self: C =>
    def rowStrValue: R => String

    def rowNodeSeqValue: R => NodeSeq = (r: R) => scala.xml.Text(rowStrValue(r))
  }

}
