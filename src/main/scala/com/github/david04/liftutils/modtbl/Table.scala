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
import net.liftweb.http.{SHtml, Templates, S}
import net.liftweb.util.{ClearNodes, ClearClearable, PassThru}
import net.liftweb.util.Helpers._
import com.github.david04.liftutils.loc.Loc
import com.github.david04.liftutils.elem.ID

trait Col {

  def tdClasses = ""

  def thClasses = ""

  def tdStyle = List[String]()

  def thStyle = List[String]()
}

trait Table extends Loc with ID {

  def table = this

  def sel(id: String): String = "$('#" + id + "')"

  def sel(id: String, rest: String): String = sel(id) + rest

  protected def tableClasses: List[String] = Nil

  trait TableCol extends Col with Loc {
    self: C =>
    override def parentLoc = table

    def renderHead: NodeSeq => NodeSeq =
      "th [class+]" #> thClasses &
        "th [style+]" #> thStyle

    def renderRow(row: R, rowId: String, idx: Int, colId: String, colIdx: Int): NodeSeq => NodeSeq =
      "td [class+]" #> tdClasses &
        "td [style+]" #> tdStyle &
        "td [id]" #> colId
  }

  /** Row type */
  type R
  /** Column type */
  type C <: TableCol

  /** Get all rows. */
  protected def rows: Seq[R]

  /** Get all columns. */
  protected def columns: Seq[C]

  protected def templatePath: List[String] = "templates-hidden" :: "modtbl-dflt" :: Nil

  def keepClasses: List[String] = Nil

  protected lazy val template = {
    val pass1 = ClearClearable(Templates(templatePath).openOrThrowException("Not found: " + templatePath.mkString("/", "/", "")))
    val pass2 = (keepClasses.map(clas => s".$clas [class!]" #> "modtbl-clearable").reduceOption(_ & _).getOrElse(PassThru)).apply(pass1)
    (".modtbl-clearable" #> ClearNodes).apply(pass2)
  }

  protected lazy val pageRenderer = SHtml.idMemoize(_ => pageTransforms())
  protected lazy val tableRenderer = SHtml.idMemoize(_ => tableTransforms())
  protected lazy val tableBodyRenderer = SHtml.idMemoize(_ => tableBodyTransforms())

  def rerenderPage() = pageRenderer.setHtml()
  def rerenderTable() = tableRenderer.setHtml()
  def rerenderTableBody() = tableBodyRenderer.setHtml()

  protected def pageTransforms(): NodeSeq => NodeSeq = ".modtbl-table [id]" #> id('table) andThen ".modtbl-table" #> tableRenderer

  protected def tableTransforms(): NodeSeq => NodeSeq = "thead tr th" #> columns.map(_.renderHead) & "tbody" #> tableBodyRenderer

  protected def tableBodyTransforms(): NodeSeq => NodeSeq = "tr" #> rowsTransforms()

  protected def rowsTransforms(): Seq[NodeSeq => NodeSeq] = rows.zipWithIndex.map(row => rowTransforms(row._1, S.formFuncName, row._2))

  protected def trStyle = List[String]()

  protected def trStylesFor(row: R, rowId: String, rowIdx: Int): List[String] = trStyle

  protected def rowTransforms(row: R, rowId: String, rowIdx: Int): NodeSeq => NodeSeq =
    "tr [class+]" #> trStylesFor(row, rowId, rowIdx).mkString(" ") &
      "tr [id]" #> rowId &
      "td" #> columns.zipWithIndex.map(col => col._1.renderRow(row, rowId, rowIdx, S.formFuncName, col._2))

  def renderedTable(): NodeSeq = {
    (".modtbl-around [class+]" #> tableClasses.mkString(" ") &
      ".modtbl-around [modtbl]" #> locPrefix &
      ".modtbl-around [id]" #> id('around) andThen
      ".modtbl-around" #> pageRenderer).apply(template)
  }

  def renderTable(): NodeSeq => NodeSeq = (_: NodeSeq) => renderedTable()
}

trait RowIdsTable extends Table {

  def rowId(row: R): String
  def rowsForIds(ids: Seq[String]): Seq[R]
}

