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
import net.liftweb.http.Templates
import net.liftweb.util.PassThru
import net.liftweb.util.Helpers._
import net.liftweb.http.js.JsCmd
import net.liftweb.http.js.JsCmds.Run


trait RowDetailsTable extends ClickableRowTable {

  protected def rowDetailsTemplatePath: List[String] = "templates-hidden" :: "modtbl-rowDetails-dflt" :: Nil

  /**
   * Template must contain: <tr><td><div class='details-contents'></div></td></tr>
   */
  protected def rowDetailsTemplate: NodeSeq = Templates(rowDetailsTemplatePath).get(0).descendant(3)

  protected var currentDetailsRow: Option[(R, JsCmd)] = None

  protected def rowDetailsClasses: List[String] = "details" :: Nil

  protected def rowDetailsContentClass: String = "details-contents"

  protected def openDetailsRow(row: R, rowId: String, rowIdx: Int): JsCmd = Run {
    sel(rowId, ".after(" + rowDetailsTransforms(row, rowId, rowIdx, false)(rowDetailsTemplate).toString().encJs + ");") +
      sel(s"$rowId-details .$rowDetailsContentClass", ".slideDown(400);")
  }

  protected def closeDetailsRow(row: R, rowId: String, rowIdx: Int): JsCmd = Run {
    sel(s"$rowId-details .$rowDetailsContentClass", ".slideUp(400, function() {" +
      sel(s"$rowId-details", ".remove();") +
      "});")
  }

  override protected def onClick(row: R, rowId: String, rowIdx: Int): JsCmd = {
    currentDetailsRow match {
      case Some((prev, close)) if prev == row =>
        currentDetailsRow = None
        close
      case None =>
        currentDetailsRow = Some((row, closeDetailsRow(row, rowId, rowIdx)))
        openDetailsRow(row, rowId, rowIdx)
      case Some((_, close)) =>
        currentDetailsRow = Some((row, closeDetailsRow(row, rowId, rowIdx)))
        close & openDetailsRow(row, rowId, rowIdx)
    }
  }

  protected def rowDetailsTransforms(row: R): NodeSeq => NodeSeq

  protected def rowDetailsTransforms(row: R, rowId: String, rowIdx: Int, visible: Boolean): NodeSeq => NodeSeq =
    "tr [id]" #> s"$rowId-details" &
      s"tr .$rowDetailsContentClass [style+]" #> (if (visible) "" else ";display:none;") andThen
      "td [colspan]" #> columns.size &
        "td [class+]" #> rowDetailsClasses.mkString(" ") &
        "td" #> rowDetailsTransforms(row)

  override protected def rowTransforms(row: R, rowId: String, rowIdx: Int): NodeSeq => NodeSeq =
    super.rowTransforms(row, rowId, rowIdx) andThen {
      currentDetailsRow match {
        case Some((open, _)) if open == row =>
          currentDetailsRow = Some((row, closeDetailsRow(row, rowId, rowIdx)))
          (ns: NodeSeq) => ns ++ rowDetailsTransforms(row, rowId, rowIdx, true)(rowDetailsTemplate)
        case _ =>
          PassThru
      }
    }
}
