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

import scala.xml.NodeSeq
import net.liftweb.http.SHtml
import net.liftweb.util.Helpers._
import net.liftweb.http.js.{JsCmds, JsCmd}
import net.liftweb.util.PassThru

trait ClickableRowTable extends Table {
  type C <: ClickableRowCol

  protected def onClick(row: R, rowId: String, rowIdx: Int, col: C, colId: String): JsCmd = JsCmds.Noop

  def isClickable(row: R, rowId: String, rowIdx: Int): Boolean = true

  trait ClickableRowCol extends TableCol {
    self: C =>

    override def renderRow(row: R, rowId: String, rowIdx: Int, colId: String): NodeSeq => NodeSeq =
      super.renderRow(row, rowId, rowIdx, colId) andThen
        (if (isClickable(row, rowId, rowIdx))
          "td [onclick]" #> SHtml.onEvent(_ => onClick(row, rowId, rowIdx, this, colId)).toJsCmd
        else PassThru)
  }

}
