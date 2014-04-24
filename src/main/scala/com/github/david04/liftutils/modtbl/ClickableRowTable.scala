package com.github.david04.liftutils.modtbl

import scala.xml.NodeSeq
import net.liftweb.http.SHtml
import net.liftweb.util.Helpers._
import net.liftweb.http.js.{JsCmds, JsCmd}

trait ClickableRowTable extends Table {
  type C <: ClickableRowCol

  protected def onClick(row: R, rowId: String, rowIdx: Int, col: C, colId: String): JsCmd = JsCmds.Noop

  trait ClickableRowCol extends TableCol {
    self: C =>

    override def renderRow(row: R, rowId: String, rowIdx: Int, colId: String): NodeSeq => NodeSeq =
      super.renderRow(row, rowId, rowIdx, colId) andThen
        "td [onclick]" #> SHtml.onEvent(_ => onClick(row, rowId, rowIdx, this, colId)).toJsCmd
  }

}
