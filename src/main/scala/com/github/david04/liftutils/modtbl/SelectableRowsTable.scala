package com.github.david04.liftutils.modtbl

import net.liftweb.http.js.JsCmds._
import net.liftweb.http.js.JsCmd


trait SelectableRowsTable extends ClickableRowTable {

  override protected def tableClasses: List[String] = "selectable-rows-table" :: super.tableClasses

  protected def selectedRowClass = "selected"
  protected var selectedRows = Set[R]()

  protected def selectedRow(row: R): JsCmd = Noop
  protected def diselectedRow(row: R): JsCmd = Noop
  protected def changedSelection(selected: Set[R]): JsCmd = Noop

  override protected def trStylesFor(row: R, rowId: String, rowIdx: Int): List[String] =
    if (selectedRows.contains(row)) selectedRowClass :: super.trStylesFor(row, rowId, rowIdx)
    else super.trStylesFor(row, rowId, rowIdx)

  override protected def onClick(row: R, rowId: String, rowIdx: Int, col: C, colId: String): JsCmd = {
    if (selectedRows.contains(row)) {
      selectedRows = selectedRows - row
      diselectedRow(row) & changedSelection(selectedRows) &
        Run(s"${'$'}('#$rowId').removeClass('$selectedRowClass')")
    } else {
      selectedRows = selectedRows + row
      selectedRow(row) & changedSelection(selectedRows) &
        Run(s"${'$'}('#$rowId').addClass('$selectedRowClass')")
    }
  }
}
