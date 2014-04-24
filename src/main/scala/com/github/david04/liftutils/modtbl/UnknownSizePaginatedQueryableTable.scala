package com.github.david04.liftutils.modtbl

import scala.xml.NodeSeq
import net.liftweb.http.Templates
import net.liftweb.util.Helpers._
import net.liftweb.http.js.JsCmds.Run
import com.github.david04.liftutils.props.Props


trait UnknownSizePaginatedQueryableTable extends PaginatedQueryableTable {

  protected lazy val nPages = Int.MaxValue / 2

  protected def paginationInfoTransforms(): NodeSeq => NodeSeq =
    ".modtbl-pag-info *" #>
      loc("pagInfo",
        "from" -> (currentPage * pageSize + 1).toString,
        "to" -> ((currentPage + 1) * pageSize).toString)
}
