package com.github.david04.liftutils.modtbl

import scala.xml.NodeSeq
import net.liftweb.http.SHtml
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
  protected lazy val defaultPageSize = 40

  protected var currentPage = 0
  protected var pageSize = defaultPageSize
  protected val nPages: Int

  override protected def prepareQuery(_query: Q): Q = {
    val query = super.prepareQuery(_query)
    query.pageSize = pageSize
    query.pageOffset = currentPage * pageSize
    query
  }

  override protected def pageTransforms() =
    super.pageTransforms() andThen
      ".modtbl-pag-btns-around" #> paginationButtonsRenderer &
        ".modtbl-pag-info-around" #> paginationInfoRenderer

  protected def firstPage() = SHtml.onEvent(_ => {
    currentPage = 0
    rerenderPage()
  }).cmd & Run("return false;")

  protected def prevPage() = SHtml.onEvent(_ => {
    currentPage = math.max(0, currentPage - 1)
    rerenderPage()
  }).cmd & Run("return false;")

  protected def toPage(n: Int) = SHtml.onEvent(_ => {
    currentPage = n
    rerenderPage()
  }).cmd & Run("return false;")

  protected def nextPage() = SHtml.onEvent(_ => {
    currentPage = math.min(nPages - 1, currentPage + 1)
    rerenderPage()
  }).cmd & Run("return false;")

  protected def lastPage() = SHtml.onEvent(_ => {
    currentPage = nPages - 1
    rerenderPage()
  }).cmd & Run("return false;")

  protected def currentButtons() = {
    val side = pagNBtns - 1
    val all = ((currentPage - side) until currentPage) ++ ((currentPage + 1) to (currentPage + side))
    all.filter(_ >= 0).filter(_ < nPages).sortBy(n => math.abs(currentPage - n)).take(side).:+(currentPage).sorted
  }

  protected lazy val paginationButtonsRenderer = SHtml.idMemoize(_ => paginationButtonsTransforms())

  protected def paginationButtonsTransforms(): NodeSeq => NodeSeq =
    ".modtbl-pag-first [class+]" #> (if (currentPage == 0) pagBtnsDisabledClass else "") &
      ".modtbl-pag-first" #> {".modtbl-pag-btn [onclick]" #> firstPage()} &
      ".modtbl-pag-prev [class+]" #> (if (currentPage == 0) pagBtnsDisabledClass else "") &
      ".modtbl-pag-prev" #> {".modtbl-pag-btn [onclick]" #> prevPage()} &
      ".modtbl-pag-next [class+]" #> (if (currentPage == nPages - 1) pagBtnsDisabledClass else "") &
      ".modtbl-pag-next" #> {".modtbl-pag-btn [onclick]" #> nextPage()} &
      ".modtbl-pag-last [class+]" #> (if (currentPage == nPages - 1) pagBtnsDisabledClass else "") &
      ".modtbl-pag-last" #> {".modtbl-pag-btn [onclick]" #> lastPage()} andThen
      ".modtbl-pag-n" #> currentButtons().map(n =>
        ".modtbl-pag-n [class+]" #> (if (currentPage == n) pagBtnsCurrentClass else "") &
          ".modtbl-pag-btn *" #> (n + 1).toString &
          ".modtbl-pag-btn [onclick]" #> toPage(n))

  protected lazy val paginationInfoRenderer = SHtml.idMemoize(_ => paginationInfoTransforms())

  protected def paginationInfoTransforms(): NodeSeq => NodeSeq
}
