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
import net.liftweb.util.{FatLazy, PassThru}
import net.liftweb.util.Helpers._
import net.liftweb.http.js.JsCmds.Run


trait SortableQueryableTable extends QueryableTable with NamedColTable {

  protected def sortThNone = "sorting"

  protected def sortThAsc = "sorting_asc"

  protected def sortThDesc = "sorting_desc"

  trait SortCol extends NamedCol {
    self: C =>

    def defaultSortAsc: Boolean = true

    def sortable: Boolean

    override def renderHead(implicit data: Data): NodeSeq => NodeSeq =
      super.renderHead andThen
        (if (sortable)
          "th [class+]" #> (if (name == data.currentSortCol.name) (if (data.currentSortAsc.get) sortThAsc else sortThDesc) else sortThNone) &
            "th [onclick]" #> clickedSortableHeader(this)
        else PassThru)
  }

  trait SortQuery extends Query {
    var sortColumn: C
    var sortAsc: Boolean
  }

  type C <: SortCol
  type Q <: SortQuery
  type Data <: DataSortableTable

  trait DataSortableTable extends TableData {

    def defaultSortCol = cols.head
    var _currentSortColName: String = defaultSortCol.name
    def currentSortCol: C = cols.find(_.name == _currentSortColName).getOrElse(defaultSortCol)
    def currentSortCol_=(c: C): Unit = _currentSortColName = c.name
    val currentSortAsc: FatLazy[Boolean] = FatLazy(currentSortCol.defaultSortAsc)

  }

  protected def clickedSortableHeader(col: C)(implicit data: Data) = SHtml.onEvent(_ => {
    val before = data.currentSortCol

    if (col.name == data.currentSortCol.name) {
      data.currentSortAsc() = !data.currentSortAsc.get
    } else {
      data.currentSortCol = col
      data.currentSortAsc() = col.defaultSortAsc
    }

    Run("$('[col-name=\"" + before.name + "\"]')" + s".removeClass('$sortThAsc').removeClass('$sortThDesc').addClass('$sortThNone')") &
      Run("$('[col-name=\"" + col.name + "\"]')" + s".removeClass('$sortThNone').addClass('" + (if (data.currentSortAsc.get) sortThAsc else sortThDesc) + "')") &
      rerenderTableBody()
  })

  override protected def prepareQuery(_query: Q)(implicit data: Data): Q = {
    val query = super.prepareQuery(_query)
    query.sortColumn = data.currentSortCol
    query.sortAsc = data.currentSortAsc.get
    query
  }
}
