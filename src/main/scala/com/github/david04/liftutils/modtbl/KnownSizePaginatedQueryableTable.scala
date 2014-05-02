package com.github.david04.liftutils.modtbl

import scala.xml.NodeSeq
import net.liftweb.util.Helpers._


trait KnownSizePaginatedQueryableTable extends PaginatedQueryableTable with KnownSizeTable {
  protected lazy val nPages = math.ceil(rowsSize / pageSize.toDouble).toInt

  protected def paginationInfoTransforms(): NodeSeq => NodeSeq =
    ".modtbl-pag-info *" #>
      loc("pagInfo",
        "from" -> (currentPage * pageSize + 1).toString,
        "to" -> ((currentPage + 1) * pageSize).toString,
        "total" -> rowsSize.toString)
}
