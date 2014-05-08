//  Copyright (c) 2014 David Miguel Antunes <davidmiguel {at} antunes.net>
//
//  Permission is hereby granted, free of charge, to any person obtaining a copy
//  of this software and associated documentation files (the "Software"), to deal
//  in the Software without restriction, including without limitation the rights
//  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
//  copies of the Software, and to permit persons to whom the Software is
//  furnished to do so, subject to the following conditions:
//
//  The above copyright notice and this permission notice shall be included in
//  all copies or substantial portions of the Software.
//
//  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
//  THE SOFTWARE.

package com.github.david04.liftutils.modtbl

import net.liftweb.http.js.JsCmds._
import net.liftweb.http.js.JsCmd


trait SelectableRowsTable extends ClickableRowTable {

  override protected def tableClasses: List[String] = "selectable-rows-table" :: super.tableClasses

  protected def selectedRowClass = "selected"
  protected def initialSelectedRows = Set[R]()
  protected var selectedRows = initialSelectedRows

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
