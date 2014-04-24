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

    override def renderHead: NodeSeq => NodeSeq =
      super.renderHead andThen
        (if (sortable)
          "th [class+]" #> (if (name == currentSortCol.name) (if (currentSortAsc.get) sortThAsc else sortThDesc) else sortThNone) &
            "th [onclick]" #> clickedSortableHeader(this)
        else PassThru)
  }

  trait SortQuery extends Query {
    var sortColumn: C
    var sortAsc: Boolean
  }

  type C <: SortCol
  type Q <: SortQuery

  protected var _currentSortColName: String = columns.head.name
  protected def currentSortCol: C = columns.find(_.name == _currentSortColName).getOrElse(columns.head)
  protected def currentSortCol_=(c: C): Unit = _currentSortColName = c.name
  protected val currentSortAsc: FatLazy[Boolean] = FatLazy(currentSortCol.defaultSortAsc)

  protected def clickedSortableHeader(col: C) = SHtml.onEvent(_ => {
    val before = currentSortCol

    if (col.name == currentSortCol.name) {
      currentSortAsc() = !currentSortAsc.get
    } else {
      currentSortCol = col
      currentSortAsc() = col.defaultSortAsc
    }

    Run("$('[col-name=\"" + before.name + "\"]')" + s".removeClass('$sortThAsc').removeClass('$sortThDesc').addClass('$sortThNone')") &
      Run("$('[col-name=\"" + col.name + "\"]')" + s".removeClass('$sortThNone').addClass('" + (if (currentSortAsc.get) sortThAsc else sortThDesc) + "')") &
      rerenderTableBody()
  })

  override protected def prepareQuery(_query: Q): Q = {
    val query = super.prepareQuery(_query)
    query.sortColumn = currentSortCol
    query.sortAsc = currentSortAsc.get
    query
  }
}
