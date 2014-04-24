package com.github.david04.liftutils.modtbl

import scala.xml.NodeSeq
import net.liftweb.http.Templates
import net.liftweb.util.Helpers._

trait ZebraTable extends Table {

  protected def zebraTableEvenClass = "even"

  protected def zebraTableOddClass = "odd"

  override protected def rowTransforms(row: R, rowId: String, rowIdx: Int): NodeSeq => NodeSeq =
    super.rowTransforms(row, rowId, rowIdx) andThen
      "tr [class+]" #> (if (rowIdx % 2 == 0) zebraTableEvenClass else zebraTableOddClass)
}
