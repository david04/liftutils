package com.github.david04.liftutils.modtbl

import scala.xml.NodeSeq
import net.liftweb.util.PassThru

abstract class DefaultTable extends Table
                                    with NamedTable
                                    with QueryableTable
                                    with PaginatedQueryableTable
                                    with SortableQueryableTable
                                    with LocStrHeadTable
                                    with StrRowTable
                                    with ZebraTable {
  type Q = DefaultQuery
  type C <: DefaultColumn

  case class DefaultQuery(
                           var pageSize: Int,
                           var pageOffset: Int,
                           var sortColumn: C,
                           var sortAsc: Boolean) extends Query
                                                         with PagQuery
                                                         with SortQuery

  def createQuery = DefaultQuery(0, 0, columns.head, false)

  abstract class DefaultColumn(
                                name: String,
                                rowValue: R => String) extends TableCol
                                                               with LocStrHeadCol
                                                               with StrRowCol
                                                               with SortCol {
    self: C =>

    def rowStrValue: R => String = rowValue
  }

}

abstract class DefaultSimpleTable extends Table
                                          with NamedTable
                                          with LocStrHeadTable
                                          with StrRowTable {
  override protected def templatePath: List[String] = "templates-hidden" :: "modtbl-simple" :: Nil

  trait DefaultColumn extends TableCol with LocStrHeadCol {}

  type C = DefaultColumn

  case class DefaultStringColumn(
                                  name: String,
                                  rowValue: R => String) extends DefaultColumn
                                                                 with StrRowCol {
    self: C =>

    def rowStrValue: R => String = rowValue
  }

  case class DefaultNodeSeqColumn(
                                   name: String,
                                   rowValue: R => NodeSeq) extends DefaultColumn
                                                                   with NodeSeqRowCol {
    self: C =>

    def rowNodeSeqValue: R => NodeSeq = rowValue
  }

}


abstract class DefaultOpenableTable extends Table
                                            with NamedTable
                                            with QueryableTable
                                            with PaginatedQueryableTable
                                            with SortableQueryableTable
                                            with RowDetailsTable
                                            with LocStrHeadTable
                                            with StrRowTable
                                            with ZebraTable {
  type Q = DefaultQuery
  type C <: DefaultColumn

  case class DefaultQuery(
                           var pageSize: Int,
                           var pageOffset: Int,
                           var sortColumn: C,
                           var sortAsc: Boolean) extends Query
                                                         with PagQuery
                                                         with SortQuery

  def createQuery = DefaultQuery(0, 0, columns.head, false)

  abstract class DefaultColumn(
                                name: String,
                                rowValue: R => String) extends TableCol
                                                               with ClickableRowCol
                                                               with LocStrHeadCol
                                                               with StrRowCol
                                                               with SortCol {
    self: C =>
  }

}







