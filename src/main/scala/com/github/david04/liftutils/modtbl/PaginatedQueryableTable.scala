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
import net.liftweb.http.{SHtml2, SHtml}
import net.liftweb.util.Helpers._
import net.liftweb.http.js.JsCmds.Run


trait PaginatedQueryableTable extends QueryableTable {

  trait PagQuery extends Query {
    var pageSize: Int
    var pageOffset: Int
  }

  type Q <: PagQuery

  override def keepClasses: List[String] = "modtbl-pag-btns-around" :: super.keepClasses

  protected lazy val pagBtnsCurrentClass = "active"
  protected lazy val pagBtnsDisabledClass = "disabled"
  protected lazy val pagNBtns = 5
  protected def defaultPageSize = 40

  type Data <: DataPaginatedQueryableTable

  trait DataPaginatedQueryableTable extends TableData {

    var currentPage = 0
    var pageSize = defaultPageSize
    val nPages: Int
  }

  override protected def prepareQuery(_query: Q)(implicit data: Data): Q = {
    val query = super.prepareQuery(_query)
    query.pageSize = data.pageSize
    query.pageOffset = data.currentPage * data.pageSize
    query
  }

  override protected def pageTransforms(implicit data: Data) =
    super.pageTransforms andThen
      ".modtbl-pag-btns-around" #> paginationButtonsRenderer(data) &
        ".modtbl-pag-info-around" #> paginationInfoRenderer(data)

  protected def firstPage()(implicit data: Data) = SHtml.onEvent(_ => {
    data.currentPage = 0
    rerenderPage()
  }).cmd & Run("return false;")

  protected def prevPage()(implicit data: Data) = SHtml.onEvent(_ => {
    data.currentPage = math.max(0, data.currentPage - 1)
    rerenderPage()
  }).cmd & Run("return false;")

  protected def toPage(n: Int)(implicit data: Data) = SHtml.onEvent(_ => {
    data.currentPage = n
    rerenderPage()
  }).cmd & Run("return false;")

  protected def nextPage()(implicit data: Data) = SHtml.onEvent(_ => {
    data.currentPage = math.min(data.nPages - 1, data.currentPage + 1)
    rerenderPage()
  }).cmd & Run("return false;")

  protected def lastPage()(implicit data: Data) = SHtml.onEvent(_ => {
    data.currentPage = data.nPages - 1
    rerenderPage()
  }).cmd & Run("return false;")

  protected def currentButtons()(implicit data: Data) = {
    val side = pagNBtns - 1
    val all = ((data.currentPage - side) until data.currentPage) ++ ((data.currentPage + 1) to (data.currentPage + side))
    all.filter(_ >= 0).filter(_ < data.nPages).sortBy(n => math.abs(data.currentPage - n)).take(side).:+(data.currentPage).sorted
  }

  protected lazy val paginationButtonsRenderer = SHtml2.idMemoize[Data]((_, data) => paginationButtonsTransforms(data))

  protected def paginationButtonsTransforms(implicit data: Data): NodeSeq => NodeSeq =
    ".modtbl-pag-first [class+]" #> (if (data.currentPage == 0) pagBtnsDisabledClass else "") &
      ".modtbl-pag-first" #> {".modtbl-pag-btn [onclick]" #> firstPage()} &
      ".modtbl-pag-prev [class+]" #> (if (data.currentPage == 0) pagBtnsDisabledClass else "") &
      ".modtbl-pag-prev" #> {".modtbl-pag-btn [onclick]" #> prevPage()} &
      ".modtbl-pag-next [class+]" #> (if (data.currentPage == data.nPages - 1) pagBtnsDisabledClass else "") &
      ".modtbl-pag-next" #> {".modtbl-pag-btn [onclick]" #> nextPage()} &
      ".modtbl-pag-last [class+]" #> (if (data.currentPage == data.nPages - 1) pagBtnsDisabledClass else "") &
      ".modtbl-pag-last" #> {".modtbl-pag-btn [onclick]" #> lastPage()} andThen
      ".modtbl-pag-n" #> currentButtons().map(n =>
        ".modtbl-pag-n [class+]" #> (if (data.currentPage == n) pagBtnsCurrentClass else "") &
          ".modtbl-pag-btn *" #> (n + 1).toString &
          ".modtbl-pag-btn [onclick]" #> toPage(n))

  protected lazy val paginationInfoRenderer = SHtml2.idMemoize[Data]((_, data) => paginationInfoTransforms(data))

  protected def paginationInfoTransforms(implicit data: Data): NodeSeq => NodeSeq
}
