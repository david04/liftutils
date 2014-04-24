package com.github.david04.liftutils.modtbl

import net.liftweb.util.Helpers._
import scala.xml._

trait EmptyTable extends KnownSizeTable {

  protected def emptyTableClass = "table-empty"

  protected def emptyTableContent: NodeSeq = Text(loc("empty"))

  override protected def rowsTransforms(): Seq[NodeSeq => NodeSeq] =
    if (rowsSize == 0)
      ("td [class+]" #> emptyTableClass &
        "td [colspan]" #> columns.size &
        "td *" #> emptyTableContent) :: Nil
    else super.rowsTransforms()
}
