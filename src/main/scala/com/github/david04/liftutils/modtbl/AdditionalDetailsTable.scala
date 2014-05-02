package com.github.david04.liftutils.modtbl

import scala.xml.NodeSeq
import net.liftweb.http.Templates
import net.liftweb.util.{Helpers, PassThru}
import net.liftweb.util.Helpers._
import net.liftweb.http.js.JsCmd
import net.liftweb.http.js.JsCmds.Run


trait AdditionalDetailsTable extends Table {

  protected def nonDetailsRowClass = "normal-tr"
  override protected def trStyle = nonDetailsRowClass :: super.trStyle

  protected def rowDetailsContents(row: R, rowId: String, rowIdx: Int): NodeSeq = NodeSeq.Empty

  protected def rowDetails(row: R, rowId: String, rowIdx: Int): NodeSeq => NodeSeq =
    "tr [id]" #> (rowId + "_details") &
      "td [colspan]" #> columns.size &
      "td *" #> rowDetailsContents(row, rowId, rowIdx)

  override protected def rowsTransforms(): Seq[NodeSeq => NodeSeq] =
    rows.zipWithIndex.flatMap(row => {
      val id = Helpers.nextFuncName
      rowTransforms(row._1, id, row._2) ::
        rowDetails(row._1, id, row._2) :: Nil
    })
}
